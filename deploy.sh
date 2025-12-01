#!/bin/bash
cd /home/ec2-user/app || exit 1

DOCKER_APP_NAME="moviezip-app"
JAR_FILE="demo-0.0.1-SNAPSHOT.jar"
LOG_DIR="./logs"

mkdir -p ${LOG_DIR}

# blue 컨테이너 실행 여부 확인
EXIST_BLUE=$(docker ps --filter "name=${DOCKER_APP_NAME}-blue" --filter "status=running" -q)

if [ -z "$EXIST_BLUE" ]; then
    echo "Deploy Blue"
    docker rm -f ${DOCKER_APP_NAME}-blue || true
    docker run -d --name moviezip-app-blue -p 8081:8080 \
      -v $(pwd)/demo-0.0.1-SNAPSHOT.jar:/app/demo-0.0.1-SNAPSHOT.jar \
      -e SPRING_PROFILES_ACTIVE=prod \
      -e DB_URL=jdbc:oracle:thin:@dblab.dongduk.ac.kr:1521/orclpdb \
      -e DB_USERNAME=ss240205 \
      -e DB_PASSWORD=ss240205ss \
      -e MONGODB_URI=mongodb://localhost:27017/movieZip \
      -e REDIS_HOST=localhost \
      -e REDIS_PORT=6379 \
      eclipse-temurin:17-jdk \
      java -jar /app/demo-0.0.1-SNAPSHOT.jar
    sleep 15
    docker rm -f ${DOCKER_APP_NAME}-green || true
else
    echo "Deploy Green"
    docker rm -f ${DOCKER_APP_NAME}-green || true
    docker run -d --name moviezip-app-green -p 8082:8080 \
      -v $(pwd)/demo-0.0.1-SNAPSHOT.jar:/app/demo-0.0.1-SNAPSHOT.jar \
      -e SPRING_PROFILES_ACTIVE=prod \
      -e DB_URL=jdbc:oracle:thin:@dblab.dongduk.ac.kr:1521/orclpdb \
      -e DB_USERNAME=ss240205 \
      -e DB_PASSWORD=ss240205ss \
      -e MONGODB_URI=mongodb://localhost:27017/movieZip \
      -e REDIS_HOST=localhost \
      -e REDIS_PORT=6379 \
      eclipse-temurin:17-jdk \
      java -jar /app/demo-0.0.1-SNAPSHOT.jar
    sleep 15
    docker rm -f ${DOCKER_APP_NAME}-blue || true
fi

docker image prune -af

echo "Deployment completed!"
echo "Blue 로그: ${LOG_DIR}/blue.log"
echo "Green 로그: ${LOG_DIR}/green.log"
