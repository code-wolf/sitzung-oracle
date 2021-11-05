FROM balenalib/raspberry-pi-openjdk:8-stretch

#RUN addgroup --system spring && adduser --system --group spring spring
#USER spring:spring

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]