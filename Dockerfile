# 1. JDK 21 기반
FROM eclipse-temurin:21-jdk

# 2. 작업 디렉토리
WORKDIR /app

# 3. 빌드 결과물 복사
COPY build/libs/*.jar app.jar

# 4. secret 폴더 복사 (Docker 이미지에 포함)
COPY src/main/resources/secret /app/secret

# 5. 실행
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.config.additional-location=file:/app/secret/"]
