#!/bin/bash
cd /home/ec2-user/app

DOCKER_APP_NAME=spring

# blue 컨테이너 실행 여부 확인
EXIST_BLUE=$(docker compose --project-name ${DOCKER_APP_NAME}-blue -f docker-compose-blue.yml ps | grep Up)

if [ -z "$EXIST_BLUE" ]; then
    echo "Deploy Blue"
    docker compose --project-name ${DOCKER_APP_NAME}-blue -f docker-compose-blue.yml up -d --build
    sleep 30
    docker compose --project-name ${DOCKER_APP_NAME}-green -f docker-compose-green.yml down
else
    echo "Deploy Green"
    docker compose --project-name ${DOCKER_APP_NAME}-green -f docker-compose-green.yml up -d --build
    sleep 30
    docker compose --project-name ${DOCKER_APP_NAME}-blue -f docker-compose-blue.yml down
fi

# 사용하지 않는 이미지 삭제
docker image prune -af
echo "Deployment completed!"
