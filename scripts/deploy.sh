#!/bin/bash

# 빌드된 JAR 파일 경로
BUILD_JAR=/home/ec2-user/daeng-ggu-backend/build/libs/daeng-guu-backend-0.0.1-SNAPSHOT.jar
JAR_NAME=$(basename $BUILD_JAR)

# 배포 로그 기록
echo "> 현재 시간: $(date)" >> /home/ec2-user/action/deploy.log
echo "> build 파일명: $JAR_NAME" >> /home/ec2-user/action/deploy.log
echo "> build 파일 복사" >> /home/ec2-user/action/deploy.log

# 배포할 디렉토리
DEPLOY_PATH=/home/ec2-user/action/

# 빌드된 JAR 파일을 배포 디렉토리로 복사
cp $BUILD_JAR $DEPLOY_PATH

# 현재 실행 중인 애플리케이션의 PID 확인
echo "> 현재 실행중인 애플리케이션 pid 확인" >> /home/ec2-user/action/deploy.log
CURRENT_PID=$(pgrep -f $JAR_NAME)

# 애플리케이션이 실행 중이면 종료
if [ -z "$CURRENT_PID" ]
then
  echo "> 현재 구동중인 애플리케이션이 없으므로 종료하지 않습니다." >> /home/ec2-user/action/deploy.log
else
  echo "> 현재 구동중인 애플리케이션 pid: $CURRENT_PID, 종료 중..." >> /home/ec2-user/action/deploy.log
  sudo kill -9 $CURRENT_PID
  sleep 5
fi

# JAR 파일 배포 경로 설정
DEPLOY_JAR=$DEPLOY_PATH$JAR_NAME
echo "> DEPLOY_JAR 배포" >> /home/ec2-user/action/deploy.log

# 애플리케이션을 백그라운드에서 실행
echo "> 애플리케이션 실행 중..." >> /home/ec2-user/action/deploy.log
sudo nohup java -jar $DEPLOY_JAR >> /home/ec2-user/deploy.log 2>/home/ec2-user/action/deploy_err.log &