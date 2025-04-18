include .env

start-docker: gen-cert
	docker compose up --build

compile:
	mvn clean package spring-boot:repackage -DskipTests

run: compile
	java -jar target/siscatharsis-0.0.1.jar

gen-cert:
	@mkdir "cert/"
	@cd "cert/"

	@echo $(KEY_STORE_FILE) $(KEY_STORE_PASS) $(KEY_PASS) $(KEY_ALIAS)

	@keytool -genkeypair -alias 'rootCA' -keyalg 'RSA' -keysize 2048 -validity 3650 \
  		-keystore $(KEY_STORE_FILE) -storepass $(KEY_STORE_PASS) -keypass $(KEY_PASS) \
  		-dname "CN=RootCA, OU=Security, O=Mtuci, L=Moscow, C=RU" \
  		-ext 'bc:c'

	@keytool -exportcert -alias 'rootCA' -keystore $(KEY_STORE_FILE) -storepass $(KEY_STORE_PASS) \
		-file 'rootCA.crt' -rfc

	@keytool -genkeypair -alias 'intermediateCA' -keyalg 'RSA' -keysize 2048 -validity 1825 \
    	-keystore $(KEY_STORE_FILE) -storepass $(KEY_STORE_PASS) -keypass $(KEY_PASS) \
    	-dname "CN=IntermediateCA, OU=Security, O=Mtuci, L=Moscow, ST=Moscow, C=RU" \
    	-ext 'bc:c'

	@keytool -certreq -alias 'intermediateCA' -keystore $(KEY_STORE_FILE) -storepass $(KEY_STORE_PASS) \
    	-file 'intermediateCA.csr'

	@keytool -gencert -alias rootCA -keystore $(KEY_STORE_FILE) -storepass $(KEY_STORE_PASS) \
    	-infile 'intermediateCA.csr' -outfile 'intermediateCA.crt' -rfc \
    	-validity 1825 -ext 'BC=0'

	@keytool -importcert -alias 'intermediateCA' -keystore $(KEY_STORE_FILE) \
    	-storepass $(KEY_STORE_PASS) -file 'intermediateCA.crt'

	@keytool -genkeypair -alias $(KEY_ALIAS) -keyalg 'RSA' -keysize 2048 -validity 365 \
      	-keystore $(KEY_STORE_FILE) -storepass $(KEY_STORE_PASS) -keypass $(KEY_PASS) \
      	-dname "CN=server.example.com, OU=IT, O=Mtuci, L=Moscow, ST=Moscow, C=RU"

	@keytool -certreq -alias $(KEY_ALIAS) -keystore $(KEY_STORE_FILE) -storepass $(KEY_STORE_PASS) \
		-file 'endEntity.csr'

	@keytool -gencert -alias 'intermediateCA' -keystore $(KEY_STORE_FILE) -storepass $(KEY_STORE_PASS) \
		-infile 'endEntity.csr' -outfile 'endEntity.crt' -rfc \
		-validity 365

	@keytool -importcert -alias $(KEY_ALIAS) -keystore $(KEY_STORE_FILE) \
      	-storepass $(KEY_STORE_PASS) -file 'endEntity.crt'

	@cd ..
	@mv "cert/$(KEY_STORE_FILE)" "src/main/resources/$(KEY_STORE_FILE)"

clean:
	rm -rf target/
	rm -rf cert/
	rm src/main/resources/server.jks

.ONESHELL: gen-cert
.PHONY: start-docker compile run gen-cert clean