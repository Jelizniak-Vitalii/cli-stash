FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY target/cli-stash.jar app.jar

VOLUME /data

ENTRYPOINT ["java", "-jar", "app.jar"]
