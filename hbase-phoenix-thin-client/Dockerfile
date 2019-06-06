FROM maven:3.5.3-jdk-8 AS build
LABEL maintainer="https://arima.eu"

COPY src /usr/src/app/src
COPY pom.xml /usr/src/app
RUN mvn -f /usr/src/app/pom.xml clean package

FROM openjdk:8-jdk-alpine
LABEL maintainer="https://arima.eu"

#/tmp is where a Spring Boot application creates working directories for Tomcat by default
VOLUME /tmp
COPY --from=build /usr/src/app/target/hbase-phoenix-thin-client-*.jar app.jar

#Add a system property pointing to "/dev/urandom" as a source of entrop to reduce Tomcat startup time
CMD /usr/bin/java -jar app.jar -Djava.security.egd=file:/dev/./urandom
EXPOSE 8080