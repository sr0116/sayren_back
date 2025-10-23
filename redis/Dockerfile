# 공식 Redis 이미지 (최신 안정 버전 + Alpine 기반)
FROM redis:7.2-alpine

# 불필요한 패키지 제거 → 이미지 최소화
RUN apk --no-cache add --virtual .build-deps bash \
  && apk del .build-deps

# 데이터 디렉토리 볼륨 마운트
VOLUME /data

# 기본 포트 오픈
EXPOSE 6379

# Redis 실행 (기본 설정)
CMD ["redis-server", "--save", "", "--appendonly", "no"]
