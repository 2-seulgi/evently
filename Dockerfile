# 1. JDK 17 슬림 이미지 사용
FROM openjdk:21-jdk-slim

# 2. JAR 파일 복사
ARG JAR_FILE=build/libs/Evently-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

# 3. 앱 실행
ENTRYPOINT ["java", "-jar", "/app.jar"]