#!/bin/bash
set -e

# 로그 파일 생성
LOG_DIR="/home/ec2-user/app/logs"
mkdir -p ${LOG_DIR}
exec > >(tee -i ${LOG_DIR}/deploy.log)
exec 2>&1

echo "Starting deployment..."

APP_NAME="moviezip-app"
IMAGE_NAME="moviezip-server:latest"

# Docker 빌드
echo "Building Docker image..."
docker build -t ${IMAGE_NAME} /home/ec2-user/app

# Blue/Green 컨테이너 이름 및 포트
BLUE_NAME="${APP_NAME}-blue"
GREEN_NAME="${APP_NAME}-green"
BLUE_PORT=8081
GREEN_PORT=8082

# 실행 중인 컨테이너 확인
RUNNING_BLUE=$(docker ps --filter "name=${BLUE_NAME}" -q)
RUNNING_GREEN=$(docker ps --filter "name=${GREEN_NAME}" -q)

# 배포할 컨테이너 결정
if [ -z "$RUNNING_BLUE" ]; then
    DEPLOY_NAME=$BLUE_NAME
    DEPLOY_PORT=$BLUE_PORT
    STOP_NAME=$GREEN_NAME
    echo "Deploying Blue..."
else
    DEPLOY_NAME=$GREEN_NAME
    DEPLOY_PORT=$GREEN_PORT
    STOP_NAME=$BLUE_NAME
    echo "Deploying Green..."
fi

# 이전 컨테이너 종료
if [ ! -z "$STOP_NAME" ]; then
    docker rm -f ${STOP_NAME} || true
fi

# 새 컨테이너 실행
docker run -d --name ${DEPLOY_NAME} -p ${DEPLOY_PORT}:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DB_URL=jdbc:oracle:thin:@dblab.dongduk.ac.kr:1521/orclpdb \
  -e DB_USERNAME=ss240205 \
  -e DB_PASSWORD=ss240205ss \
  -e MONGODB_URI=mongodb://mongo:27017/movieZip \
  -e REDIS_HOST=redis \
  -e REDIS_PORT=6379 \
  -e JWT_SECRET="VlwEyVBsYt9V7zq57TejMnVUyzblYcfPQye08f7MGVA9XkHa" \
  ${IMAGE_NAME}
  java -jar /app/app.jar
sleep 10

# 불필요한 이미지 정리
docker image prune -af

echo "Deployment completed!"
