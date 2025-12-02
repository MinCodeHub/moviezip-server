# 1. 빌드 단계
FROM maven:3.9.3-eclipse-temurin-17 AS build
WORKDIR /app

# Maven pom.xml 먼저 복사 후 의존성 다운로드
COPY demo/pom.xml .
RUN mvn dependency:go-offline -B

# 소스 복사
COPY demo/src ./src

# JAR 빌드
RUN mvn clean package -DskipTests

# 2. 실행 단계
FROM eclipse-temurin:17-jdk-focal
WORKDIR /app

# 빌드된 JAR만 복사
COPY --from=build /app/target/demo-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
