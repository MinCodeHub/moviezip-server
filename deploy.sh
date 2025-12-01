#!/bin/bash
cd /home/ec2-user/app

# 1. 기존 컨테이너 종료 및 제거
docker ps -aq --filter "name=moviezip" | xargs -r docker stop
docker ps -aq --filter "name=moviezip" | xargs -r docker rm

# 2. zip 해제
unzip -o spring-build.zip -d ./deploy

# 3. Docker 이미지 빌드
docker build -t moviezip ./deploy

# 4. 기존 이미지 제거 (선택)
docker images -q --filter "dangling=true" | xargs -r docker rmi

# 5. Docker 컨테이너 실행
docker run -d --name moviezip -p 8080:8080 moviezip
