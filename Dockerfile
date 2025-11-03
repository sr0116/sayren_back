  GNU nano 7.2                                              Dockerfile
# 1. JDK 21 기반 이미지 사용
FROM eclipse-temurin:21-jdk

# 2. 작업 디렉토리 설정
WORKDIR /app

# 3. 빌드된 JAR 파일 복사
COPY build/libs/*.jar /app/app.jar

# 4. secret 폴더 복사 (정확히 /app/secret으로)
COPY ./src/main/resources/secret/ /app/secret/

# 5. 실행 명령어
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.config.additional-location=file:/app/secret/"]









