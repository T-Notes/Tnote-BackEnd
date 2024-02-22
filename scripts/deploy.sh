#!/bin/bash
export TZ="Asia/Seoul"

NOW=$(date +%c)

CONTAINER_ID=$(docker container ls -f "name=tnote_1" -q)

echo "[$NOW] > 컨테이너 ID: ${CONTAINER_ID}" >> /home/ubuntu/cicd/deploy.log

if [ -z ${CONTAINER_ID} ]
then
  echo "[$NOW] > 현재 구동중인 애플리케이션이 없으므로 종료하지 않습니다." >> /home/ubuntu/cicd/deploy.log
else
  echo "[$NOW] > docker stop ${CONTAINER_ID}" >> /home/ubuntu/cicd/deploy.log
  sudo docker stop ${CONTAINER_ID}
  echo "[$NOW] > docker rm ${CONTAINER_ID}" >> /home/ubuntu/cicd/deploy.log
  sudo docker rm ${CONTAINER_ID}
  echo "[$NOW] > docker rmi -f j9972/tnote " >> /home/ubuntu/cicd/deploy.log
  sudo docker rmi -f j9972/tnote
  sleep 5
fi

export ENCRYPTED_PASSWORD_FILE="/home/ubuntu/cicd/jasypt.txt"

docker run \
  --name=tnote_1 \
  --restart unless-stopped \
  -e JAVA_OPTS=-Djasypt.encryptor.password=$(cat "$ENCRYPTED_PASSWORD_FILE") \
  -e TZ=Asia/Seoul \
  -p 80:8080 \
  -d \
  --net mybridge \
  j9972/tnote

echo "[$NOW] > tnote server start!! welcome to T-note " >> /home/ubuntu/cicd/deploy.log