FROM maven:3.9-sapmachine-21 AS build-stage
WORKDIR build
COPY pom.xml pom.xml
COPY src/ src/
RUN --mount=type=cache,target=/root/.m2 mvn clean package spring-boot:repackage -DskipTests

FROM sapmachine:21-jre-ubuntu AS run
WORKDIR app
COPY --from=build-stage /build/target/siscatharsis-*.jar app.jar
EXPOSE 12321
ENTRYPOINT ["java", "-jar", "app.jar"]
