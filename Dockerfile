FROM openjdk:22-jdk

# arg by default if not specified explicilty (either via build-args or args in docker-compose.yml)
ARG JAR="siscatharsis-0.0.1.jar"

# if this fails, try to build :)
COPY target/${JAR} app.jar

EXPOSE 12321

ENTRYPOINT ["java","-jar","/app.jar"]
