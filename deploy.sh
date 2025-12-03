#!/bin/bash
set -e

# 로그 파일 생성
LOG_DIR="/home/ec2-user/app/logs"
mkdir -p ${LOG_DIR}
exec > >(tee -i ${LOG_DIR}/deploy.log)
exec 2>&1

if [ -f .env ]; then
  export $(grep -v '^#' .env | xargs)
fi

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
RUNNING_BLUE=$(docker ps -q -f name=${BLUE_NAME})
RUNNING_GREEN=$(docker ps -q -f name=${GREEN_NAME})

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

# 이전 컨테이너 종료 (존재하면 Exited 포함)
STOP_CONTAINER=$(docker ps -a -q -f name=${STOP_NAME})
if [ -n "$STOP_CONTAINER" ]; then
    echo "Stopping old container: ${STOP_NAME}"
    docker ps -a -f name=$STOP_NAME   # 삭제 전 확인용 로그
    docker rm -f $STOP_CONTAINER || true
fi

# 환경변수 값 확인 (배포 전)
echo "=== Environment Variables ==="
echo "SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE}"
echo "DB_URL=${DB_URL}"
echo "DB_USERNAME=${DB_USERNAME}"
echo "DB_PASSWORD=${DB_PASSWORD}"
echo "MONGODB_URI=${MONGODB_URI}"
echo "REDIS_HOST=${REDIS_HOST}"
echo "REDIS_PORT=${REDIS_PORT}"
echo "JWT_SECRET=${JWT_SECRET}"
echo "============================="

# 새 컨테이너 실행 (GitHub Actions에서 환경변수 주입)
docker run -d --name ${DEPLOY_NAME} -p ${DEPLOY_PORT}:8080 \
  -e SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE} \
  -e DB_URL=${DB_URL} \
  -e DB_USERNAME=${DB_USERNAME} \
  -e DB_PASSWORD=${DB_PASSWORD} \
  -e MONGODB_URI=${MONGODB_URI} \
  -e REDIS_HOST=${REDIS_HOST} \
  -e REDIS_PORT=${REDIS_PORT} \
  -e JWT_SECRET=${JWT_SECRET} \
  ${IMAGE_NAME} \
  java -jar /app/app.jar

sleep 10

# 불필요한 이미지 정리
docker image prune -af

echo "Deployment completed!"
