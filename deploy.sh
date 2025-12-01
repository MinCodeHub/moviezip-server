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
    docker run -d --name ${DOCKER_APP_NAME}-blue -p 8080:8080 \
        -v $(pwd)/${JAR_FILE}:/app/${JAR_FILE} \
        eclipse-temurin:17-jdk \
        java -jar /app/${JAR_FILE} \
        >> ${LOG_DIR}/blue.log 2>&1
    sleep 15
    docker rm -f ${DOCKER_APP_NAME}-green || true
else
    echo "Deploy Green"
    docker rm -f ${DOCKER_APP_NAME}-green || true
    docker run -d --name ${DOCKER_APP_NAME}-green -p 8081:8080 \
        -v $(pwd)/${JAR_FILE}:/app/${JAR_FILE} \
        eclipse-temurin:17-jdk \
        java -jar /app/${JAR_FILE} \
        >> ${LOG_DIR}/green.log 2>&1
    sleep 15
    docker rm -f ${DOCKER_APP_NAME}-blue || true
fi

docker image prune -af

echo "Deployment completed!"
echo "Blue 로그: ${LOG_DIR}/blue.log"
echo "Green 로그: ${LOG_DIR}/green.log"
