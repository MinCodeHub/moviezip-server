FROM openjdk:17-jdk-slim
WORKDIR /app
COPY . .
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "demo-0.0.1-SNAPSHOT.jar"]
