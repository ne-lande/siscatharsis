start-docker:
	docker compose up --build

compile:
	mvn clean package spring-boot:repackage -DskipTests

run: compile
	java -jar target/siscatharsis-0.0.1.jar