#!/usr/bin/env bash

# 애플리케이션이 배포될 경로
REPOSITORY=/home/ec2-user/app

echo "> 현재 구동 중인 애플리케이션 pid 확인"

# 8080 포트를 사용하는 현재 애플리케이션의 pid를 확인
CURRENT_PID=$(sudo lsof -t -i:8080)

echo "현재 구동 중인 애플리케이션 pid: $CURRENT_PID"

if [ -z "$CURRENT_PID" ]; then
  echo "현재 구동 중인 애플리케이션이 없으므로 종료하지 않습니다."
else
  echo "> kill -9 $CURRENT_PID"
  sudo kill -9 $CURRENT_PID
  sleep 5
fi

echo "> 새 애플리케이션 배포"

# 최신 JAR 파일 찾기
JAR_NAME=$(ls -tr $REPOSITORY/*SNAPSHOT.jar | tail -n 1)

echo "> JAR NAME: $JAR_NAME"

echo "> $JAR_NAME 에 실행권한 추가"

# 실행 권한 부여
chmod +x $JAR_NAME

echo "> $JAR_NAME 실행"

# 새 애플리케이션을 백그라운드에서 실행 (nohup 사용)
nohup java -jar -Duser.timezone=Asia/Seoul $JAR_NAME >> $REPOSITORY/nohup.out 2>&1 &