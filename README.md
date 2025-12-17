# SAREN Backend

렌탈 · 구독 비즈니스 모델 기반 커머스 플랫폼 백엔드

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.5-6DB33F?style=flat&logo=springboot&logoColor=white)
![Java](https://img.shields.io/badge/Java-21-007396?style=flat&logo=java&logoColor=white)
![MariaDB](https://img.shields.io/badge/MariaDB-10.x-003545?style=flat&logo=mariadb&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-Session%20%26%20Cache-DC382D?style=flat&logo=redis&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-Authentication-000000?style=flat)
![Docker](https://img.shields.io/badge/Docker-Deploy-2496ED?style=flat&logo=docker&logoColor=white)
![AWS](https://img.shields.io/badge/AWS-EC2%20%7C%20RDS%20%7C%20S3-FF9900?style=flat&logo=amazonaws&logoColor=white)

---

## Overview

**SIREN**은 렌탈·구독 비즈니스 모델을 기반으로 한 커머스 플랫폼입니다.  
본 저장소는 SIREN 서비스의 **백엔드 영역**을 담당하며,  
렌탈 상품 관리, 구독 계약, 결제·환불 처리, 인증·인가, 관리자 기능을 포함한  
**핵심 비즈니스 로직과 REST API**를 제공합니다.

실제 서비스 운영 환경을 가정하여  
결제 상태 관리, 구독 라이프사이클, 데이터 정합성, 보안 구조를 중심으로  
설계 및 구현되었습니다.

---
## Responsibilities 

- 렌탈·구독 도메인을 중심으로 백엔드 REST API 설계 및 구현
- 구독 신청, 진행, 해지 등 구독 상태 라이프사이클 관리 로직 개발
- NICEPAY 테스트 가맹점 계정을 사용한 **실결제 연동 및 결제 이벤트 처리**
- 결제 상태(PENDING / PAID / CANCEL) 변경에 따른 서버 측 상태 관리
- 환불 및 부분 환불 로직 구현  
  - 테스트 환경 제약에 따라 결제 취소 API 기반으로 상태 처리
- 정기 결제 빌링키 방식 대신  
  **서버 스케줄러 기반 결제 처리 구조로 대체 구현**
- 스케줄 실행 시 결제 대상 검증 및 중복 결제 방지 로직 적용
- 프론트엔드와 연동되는 REST API 설계 및 구현
- 결제·구독 흐름 전반에 대한 예외 처리 및 상태 검증 로직 작성

---

## Domain Overview

- **Member**: 사용자 및 관리자 계정 관리
- **Product**: 렌탈 상품 정보 관리
- **Subscribe / Lease**: 구독 계약 및 상태 관리
- **Payment**: 결제 요청, 완료, 실패, 환불 처리
- **Refund**: 환불 요청 및 승인 처리
- **Delivery**: 배송 상태 관리
- **Admin**: 관리자 권한 및 운영 기능

각 도메인은  
상태 변화와 책임이 명확히 분리되도록 설계되었습니다.

---

## Tech Stack

### Backend
- Java 21
- Spring Boot 
- Spring MVC
- Spring Security
- JPA사용
- MapStruct (DTO ↔ Entity 매핑)
- Lombok

### Database
- MariaDB 10.x (AWS RDS)
- Redis (세션 / 캐시 / 토큰 저장)

### Authentication
- JWT (HttpOnly Cookie 기반 인증)

### Payment
- PortOne SDK (V2)
- 일반 결제 / 정기 결제(빌링키) -  대체 스케줄링 처리로 알림 으로 결제 연결 및 회차리스트에서 결제 가능하게 변경

### Crawling
- Selenium (상품 데이터 자동 수집)

### Infra / Deployment
- AWS EC2
- AWS RDS
- AWS S3
- Docker
- Nginx

### Test
- JUnit (단위 / 통합 테스트)
- postMan
---

## Project Structure

src/main/java
 ├─ config/          # 보안, Redis, Web 설정
 ├─ controller/      # REST API Controller
 ├─ service/         # 비즈니스 로직
 ├─ domain/          # Entity / Domain Model
 ├─ dto/             # Request / Response DTO
 ├─ mapper/          # MyBatis Mapper
 ├─ repository/      # JPA Repository
 ├─ exception/       # 커스텀 예외 및 공통 처리
 └─ util/            # 공통 유틸

