# 1. JDK 21 기반 이미지
FROM eclipse-temurin:21-jdk

# 2. 작업 디렉토리 설정
WORKDIR /app

# 3. Gradle 빌드 결과물 복사
COPY build/libs/*.jar app.jar

# 4. Spring Boot 실행 명령어
ENTRYPOINT ["java", "-jar", "app.jar"]
