FROM maven:3.8.4-openjdk-8 AS build
COPY ./src /usr/src/app/src
COPY ./pom.xml /usr/src/app/pom.xml
RUN mvn -f /usr/src/app/pom.xml clean package

#FROM balenalib/raspberry-pi-openjdk:8-stretch AS runner
FROM openjdk:17.0.1-slim-buster
#RUN addgroup --system spring && adduser --system --group spring spring
#USER spring:spring

COPY --from=build /usr/src/app/target/*.jar /app.jar
#ARG JAR_FILE=target/*.jar
#COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]