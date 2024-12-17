FROM maven:3.9-sapmachine-21 AS build-stage

# Cache the dependencies

WORKDIR build

COPY pom.xml pom.xml
#RUN mvn dependency:go-offline -B

COPY src/ src/
#RUN mvn -f pom.xml package -DskipTests

#CMD ["java", "-jar", "/build/target/siscatharsis-0.0.1.jar"]
ENTRYPOINT ["mvn", "spring-boot:run"]
# should use 21 and jre, but i dont care
#FROM sapmachine:21-jre-ubuntu AS run

#WORKDIR app
# arg by default if not specified explicilty (either via build-args or args in docker-compose.yml)
#ARG JAR="siscatharsis-0.0.1.jar"

# if this fails, try to build :)
#COPY --from=build-stage /build/target/${JAR} app.jar

#EXPOSE 12321

#ENTRYPOINT ["java", "-jar", "app.jar"]
