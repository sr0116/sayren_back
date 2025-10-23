# 1. JDK 21 기반
FROM eclipse-temurin:21-jdk

# 2. 작업 디렉토리
WORKDIR /app

# 3. 빌드 결과물 복사
COPY build/libs/*.jar app.jar

# 4. secret 폴더 복사 (EC2에서만 존재)
COPY src/main/resources/secret /app/resources/secret

# 5. 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
