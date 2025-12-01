#!/bin/bash
cd /home/ec2-user/app

DOCKER_APP_NAME=spring
JAR_FILE="demo-0.0.1-SNAPSHOT.jar"

# blue 컨테이너 실행 여부 확인
EXIST_BLUE=$(docker ps --filter "name=${DOCKER_APP_NAME}-blue" --filter "status=running" -q)

if [ -z "$EXIST_BLUE" ]; then
    echo "Deploy Blue"
    docker run -d --name ${DOCKER_APP_NAME}-blue -p 8080:8080 -v $(pwd)/${JAR_FILE}:/app/${JAR_FILE} openjdk:17 java -jar /app/${JAR_FILE}
    sleep 15
    docker rm -f ${DOCKER_APP_NAME}-green || true
else
    echo "Deploy Green"
    docker run -d --name ${DOCKER_APP_NAME}-green -p 8080:8080 -v $(pwd)/${JAR_FILE}:/app/${JAR_FILE} openjdk:17 java -jar /app/${JAR_FILE}
    sleep 15
    docker rm -f ${DOCKER_APP_NAME}-blue || true
fi

# 사용하지 않는 이미지 삭제
docker image prune -af
echo "Deployment completed!"
