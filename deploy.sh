#!/bin/bash
cd /home/ec2-user/app || exit 1

DOCKER_APP_NAME=spring
JAR_FILE="demo-0.0.1-SNAPSHOT.jar"
LOG_DIR="./logs"

# 로그 디렉토리 생성
mkdir -p ${LOG_DIR}

# blue 컨테이너 실행 여부 확인
EXIST_BLUE=$(docker ps --filter "name=${DOCKER_APP_NAME}-blue" --filter "status=running" -q)

if [ -z "$EXIST_BLUE" ]; then
    echo "Deploy Blue"
    # 기존 컨테이너 제거 (죽어있으면)
    docker rm -f ${DOCKER_APP_NAME}-blue || true
    # Blue 컨테이너 실행, 로그 파일로 출력
    docker run -d --name ${DOCKER_APP_NAME}-blue -p 8080:8080 \
        -v $(pwd)/${JAR_FILE}:/app/${JAR_FILE} \
        openjdk:17-jdk java -jar /app/${JAR_FILE} \
        >> ${LOG_DIR}/blue.log 2>&1
    sleep 15
    # Green 컨테이너 제거
    docker rm -f ${DOCKER_APP_NAME}-green || true
else
    echo "Deploy Green"
    docker rm -f ${DOCKER_APP_NAME}-green || true
    docker run -d --name ${DOCKER_APP_NAME}-green -p 8080:8080 \
        -v $(pwd)/${JAR_FILE}:/app/${JAR_FILE} \
        openjdk:17-jdk java -jar /app/${JAR_FILE} \
        >> ${LOG_DIR}/green.log 2>&1
    sleep 15
    docker rm -f ${DOCKER_APP_NAME}-blue || true
fi

# 사용하지 않는 이미지 삭제
docker image prune -af

echo "Deployment completed!"
echo "컨테이너 로그 확인:"
echo "Blue: ${LOG_DIR}/blue.log"
echo "Green: ${LOG_DIR}/green.log"
