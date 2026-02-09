FROM eclipse-temurin:17-jre-alpine

WORKDIR /pettersson

COPY target/*.jar pettersson.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "pettersson.jar"]
