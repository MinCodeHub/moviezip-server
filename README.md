# 🎬 영화 리뷰·예매 플랫폼 | MOVIE.ZIP (2024.03 ~ 2024.06)

> 본 저장소는 원본 Moviezip 프로젝트에서
제가 직접 기여한 핵심 영역(JWT 인증 / 캐시 성능 개선 / 실시간 채팅 / ERD 구조 리팩토링) 중심으로 정리한 포크 버전입니다.

> 🧩 Spring Security 기반 세션 인증을 JWT 구조로 전환하고, Redis 캐시를 활용한 API 응답 속도 개선,
WebSocket을 이용한 실시간 채팅 기능 및 DB 구조 개선(ERD 리디자인) 을 주도한 백엔드 프로젝트입니다.

---
<p align="center">
  <img src="https://github.com/Munhak-Zip/Moviezip_Back/assets/110006845/8f2ead82-be3e-4fb0-a2ec-04430d1a8cf5">
</p>

<br>

| 회원가입 | 로그인 | 취향 선택  |
|:-:|:-:|:-:|
| <img src="https://github.com/user-attachments/assets/5a32011f-1dd4-4dd9-ac99-13b8edf4ad8c" alt="회원가입" width="450"/> | <img src="https://github.com/user-attachments/assets/8e81fe3e-56b8-4f6f-a9d0-bba771d12118" alt="로그인" width="450"/> | <img src="https://github.com/user-attachments/assets/c6433997-3e3e-4a57-adac-05dcb0d34660" alt="취향선택" width="450"/>

| 메인화면 최신영화 | 메인화면 추천영화 | 영화 검색  |
|:-:|:-:|:-:|
| <img src="https://github.com/user-attachments/assets/c474ea04-9c53-4015-a41b-f177336766b2" alt="최신영화" width="450"/> | <img src="https://github.com/user-attachments/assets/7ee5c7b3-a962-4d32-911c-4c97bdf1131b" alt="추천영화" width="450"/> | <img src="https://github.com/user-attachments/assets/1d069c69-9854-4832-baff-c3a400caca1c" alt="영화검색" width="450"/>

| 영화 상세조회 | 리뷰 작성 | 영화 스크랩  |
|:-:|:-:|:-:|
| <img src="https://github.com/user-attachments/assets/440e360b-8db0-46f3-9cf2-c2e4e0c4ecee" alt="영화상세보기" width="450"/> | <img src="https://github.com/user-attachments/assets/9c7b0390-47a8-4b18-9c6b-95faea3af083" alt="리뷰작성" width="450" height="190"/> | <img src="https://github.com/user-attachments/assets/4226bd46-3477-4651-b204-91d4e1572398" alt="영화스크랩" width="450" height="190"/>

| 내가 좋아한 영화 | 내가 작성한 리뷰   | 리뷰 수정 및 삭제  |
|:-:|:-:|:-:|
| <img src="https://github.com/user-attachments/assets/f8c1fb57-10ff-445b-8806-3fa2eaf79b89" alt="내가좋아한영화" width="450" height="190"/> | <img src="https://github.com/user-attachments/assets/bfe5b9ee-68f5-4470-9853-1b585896ac54" alt="리뷰리스트" width="450" height="190"/> | <img src="https://github.com/user-attachments/assets/7c1d32e1-5fd8-491b-b815-bf786b3101e4" alt="리뷰 수정" width="450" height="190"/>


| 영화관 선택 화면 | 영화 좌석 예매 화면   | 결제화면  |
|:-:|:-:|:-:|
| <img src="https://github.com/user-attachments/assets/4719f0dc-9441-4786-bfb5-d66e7d557d4b" alt="영화관선택화면" width="450" height="190"/> | <img src="https://github.com/user-attachments/assets/bf4d9a2a-0237-4500-8945-d6b472161f07" alt="영화좌석예매화면" width="450" height="190"/> | <img src="https://github.com/user-attachments/assets/fda9fa59-9c59-45aa-90bd-70d1e02b53eb" alt="결제화면" width="450" height="190"/>

| 마이페이지 | 비밀번호 변경  | 아이디 찾기  |
|:-:|:-:|:-:|
| <img src="https://github.com/user-attachments/assets/541a1bc7-74b7-40b3-921a-f1dc0c078514" alt="마이페이지" width="450" height="190"/> | <img src="https://github.com/user-attachments/assets/db1502f1-b064-49b2-b622-7d23cb87f9a1" alt="비밀번호 변경" width="450" height="190"/> | <img src="https://github.com/user-attachments/assets/2789237d-13d2-4b98-b357-dd4b8b28f16a" alt="아이디찾기" width="450" height="190"/>


| 채팅방 리스트 | 채팅 화면  | 로그아웃  |
|:-:|:-:|:-:|
| <img src="https://github.com/user-attachments/assets/ccebfa2e-184d-484f-b788-2515307da38f" alt="채팅방리스트" width="450" height="190"/> | <img src="https://github.com/user-attachments/assets/9e52b7b0-c7bb-4d9c-a060-97cbc7f86502" alt="채팅화면" width="450" height="190"/> | <img src="https://github.com/user-attachments/assets/699278da-856c-4ca9-a9c0-c932ec330c99" alt="로그아웃" width="450" height="190"/>

## 📖 프로젝트 개요
- 영화 리뷰, 예매, 추천, 채팅까지 통합된 영화 플랫폼
- 팀 구성: 풀스택 5
- 역할: **추천 API 성능 개선, 인증 구조 전환, 실시간 채팅 문의 구현, DAO 구현**

---

## ⚒️ 기술 스택
| 분야 | 기술 |
|------|------|
| **Backend** | Java 17, Spring Boot, MyBatis, Spark, WebSocket |
| **Frontend** | React |
| **Database** | Oracle, MongoDB, Redis |
| **Security** | Spring Security + JWT |

---

 # 🧱 ERD 구조

### 📊 초기 ERD (초기 설계)

> 리뷰/스크랩 테이블이 중복되고, 예매/채팅 기능이 단일 테이블로 단순화되어 있어 확장성이 제한된 구조였습니다.

<img width="800" alt="image" src="https://github.com/user-attachments/assets/51ecdcdc-70c6-48ad-9192-2fcd92595fb2" />

### 📊 **최종 ERD — 리뷰·스크랩 통합 + 예매·채팅 기능 확장 구조**
> 리뷰 테이블을 통합하고, 스크랩 기능(영화/리뷰)을 분리하여 관리
>
> 실제 예매 프로세스(상영정보 → 좌석 → 결제)와 실시간 문의(1:1 채팅) 기능을 반영한 최종 구조
> 
> Oracle 기반의 주요 엔티티와 MongoDB 기반의 채팅 데이터를 분리하여 결합도 최소화
<img width="800" alt="image" src="https://github.com/user-attachments/assets/b193a3df-38c3-453d-aa00-e89e49fd95a7" />

## 🚀 주요 기여
### 🧩 1. JWT 기반 인증 구조 전환
- 기존 세션 기반 인증은 서버 측 상태를 유지해야 하므로, 확장성과 유지보수에 한계 존재
- 모바일·웹 통합 환경에서 세션 공유 문제가 발생 → Stateless한 JWT 구조로 전환
- Access / Refresh Token 구조로 토큰 재발급 로직을 분리, 보안성과 유효성 관리 향상

> ✅ 결과: 서버 세션 부하 제거

---

### ⚡ 2. 추천 API 캐싱 최적화
- 기존 Spark ALS 기반 추천 로직의 평균 응답 시간이 약 18초로 지연되는 문제 발생
- Redis Cache-aside 패턴 도입 및 24시간 TTL 설정을 통해 재계산 부담 최소화
- 첫 요청 시 Spark 연산 결과를 캐싱하고, 이후 동일 사용자 요청은 캐시에서 즉시 응답
- Cache Key 규칙(recommend:{userId})을 정의하여 사용자별 데이터 일관성 유지
  <img width="600" alt="redis-cache-architecture" src="https://github.com/user-attachments/assets/9f7fc298-a2d0-4c8d-985b-3dd31c71cb2f" />

> ✅ 결과: Redis 캐시 적용으로 API 응답 속도를 수 초 → 수 ms 수준으로 개선하며, 사용자 대기 시간을 단축함 (Spark ALS 기반 추천 로직은 팀원 구현, 캐싱 로직 및 응답 구조 개선은 직접 담당)

---

### 💬 3. WebSocket 기반 실시간 1:1 채팅 구현
- 사용자 ↔ 관리자 간 실시간 문의 기능을 위해 Spring WebSocket 기반 양방향 통신 구조 설계
- MongoDB를 별도 데이터 스토어로 분리해 비정형 채팅 데이터를 독립적으로 관리
- Oracle 기반 메인 데이터와 분리 설계하여 시스템 간 결합도를 최소화
---

### 🗂 4. 데이터 접근 계층 설계 및 SQL 매핑
- MyBatis 기반 DAO 계층을 직접 설계하여 서비스 로직과 데이터 접근 로직 분리
- Mapper XML에서 @Param과 resultMap을 활용해 DTO ↔ DB 매핑 제어
- 사용자 정보 기반 식별(findUserIdByInfo) 등, 비즈니스 요구사항에 맞는 쿼리를 직접 정의

---

### 🧠 기술 선택 이유
| 기술 | 선택 이유 |
|------|-------------|
| **Redis** | Spark 연산 재계산 방지 |
| **JWT** | Stateless 인증으로 모바일/웹 확장성 확보 |
| **MongoDB** | 비정형 채팅 데이터 유연 저장 |
---

## ✍️ 기술 블로그
- [JWT 인증 전환 회고](https://dragonair148.tistory.com/entry/Spring-Security-JWT-%EC%84%B8%EC%85%98-%EA%B8%B0%EB%B0%98-%EC%9D%B8%EC%A6%9D%EC%97%90%EC%84%9C-JWT%EB%A1%9C-%EC%A0%84%ED%99%98-%ED%9A%8C%EA%B3%A0)
- [Spring Security + JWT 사용자 인증 - Refresh Token을 활용한 인증 갱신](https://dragonair148.tistory.com/entry/Spring-Spring-Security-JWT-%EC%82%AC%EC%9A%A9%EC%9E%90-%EC%9D%B8%EC%A6%9D3-Refresh-Token%EC%9D%84-%ED%99%9C%EC%9A%A9%ED%95%9C-%EC%9D%B8%EC%A6%9D-%EA%B0%B1%EC%8B%A0)

## 📫 Contact
**GitHub:** [@MinCodeHub](https://github.com/MinCodeHub)  
**Email:** gjalsdud1030@naver.com
