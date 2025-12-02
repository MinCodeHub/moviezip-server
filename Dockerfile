# 실행 단계만 사용
FROM eclipse-temurin:17-jdk-focal
WORKDIR /app

# 이미 빌드된 JAR 복사
COPY demo-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

# 컨테이너 시작 시 JAR 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
