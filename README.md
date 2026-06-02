# Dear Future

> 미래의 나에게 보내는 디지털 타임캡슐 서비스

Dear Future는 사용자가 현재의 기록을 저장하고,
미래의 특정 시점이 되었을 때 다시 열람할 수 있는
디지털 타임캡슐 서비스입니다.

단순 메모장이 아니라,

> “현재의 나와 미래의 나를 연결하는 기록 서비스”

를 목표로 합니다.

---

# ✨ Features

## Authentication

- 회원가입
- 로그인
- JWT 인증
- 인증 기반 사용자 권한 관리

---

## Time Capsule

- 타임캡슐 생성
- 수정 / 삭제
- 목록 조회
- 상세 조회
- 미래 공개 날짜 설정

---

## Time Lock System

- 공개일 이전:
  - 캡슐 잠금 상태 유지
- 공개일 이후:
  - 자동 열람 가능

시간 기반 상태 제어 로직을 핵심 기능으로 사용합니다.

---

## File Upload

- 이미지 업로드
- AWS S3 저장
- 캡슐당 1~3장 첨부 가능

---

## Notification

- 공개일 도달 시 이메일 발송
- Spring Scheduler 기반 처리 예정

---

# 🛠 Tech Stack

## Backend

- Java
- Spring Boot
- Spring Security
- JWT

---

## Database

- postgresql

---

## Infra

- AWS EC2
- AWS S3
- Docker
- Docker Compose
- nginx

---

# 🏗 Architecture

```text
Client
   ↓
REST API
   ↓
Spring Boot
   ↓
MySQL
   ↓
AWS S3
```

---

# 📁 Project Structure

```text
src/main/java/com/dearfuture
├── auth
├── capsule
├── user
├── global
│   ├── config
│   ├── security
│   ├── exception
│   └── util
└── infrastructure
    ├── s3
    └── mail
```

---

# 📌 Core Concepts

## 1. Time-Based Access Control

캡슐은 미래의 특정 시점까지 열람할 수 없습니다.

```java
if (now.isBefore(capsule.getOpenAt())) {
    throw new CapsuleLockedException();
}
```

---

## 2. Emotional Record Service

Dear Future는 단순 SNS가 아니라,

- 시간이 지나야 가치가 생기는 데이터
- 미래의 자신에게 남기는 기록
- 감정 회고 경험

을 중심으로 설계되었습니다.

---

# 📦 Database Design

## User

| Column | Description |
|---|---|
| id | 사용자 ID |
| email | 이메일 |
| password | 비밀번호 |
| nickname | 닉네임 |
| created_at | 생성일 |

---

## Capsule

| Column | Description |
|---|---|
| id | 캡슐 ID |
| user_id | 작성자 |
| title | 제목 |
| content | 내용 |
| open_at | 공개일 |
| created_at | 생성일 |

---

## CapsuleImage

| Column | Description |
|---|---|
| id | 이미지 ID |
| capsule_id | 캡슐 ID |
| image_url | 이미지 URL |

---

# 🚀 Deployment Goal

초기 MVP는 AWS 프리티어 기반 운영을 목표로 합니다.

- 단일 EC2 인스턴스
- Docker Compose 기반 배포
- nginx Reverse Proxy
- 비용 최소화 중심 설계

---

# 🎯 Project Goals

- 실제 서비스처럼 보이는 프로젝트 완성
- 백엔드 중심 포트폴리오 구축
- AWS 운영 경험 확보
- 혼자 끝까지 완성 가능한 규모 유지

---

# 📅 Development Roadmap

## Step 1

- 회원가입
- 로그인
- JWT 인증

---

## Step 2

- 타임캡슐 CRUD

---

## Step 3

- 공개일 잠금 로직 구현

---

## Step 4

- S3 이미지 업로드

---

## Step 5

- 이메일 알림 기능 구현

---

# 🧠 What I Want to Focus On

이 프로젝트를 통해 다음 역량을 강화하는 것을 목표로 합니다.

- REST API 설계
- 인증/인가 처리
- 시간 기반 비즈니스 로직
- AWS 인프라 운영
- 파일 업로드 처리
- Docker 기반 배포 경험

---

# 📄 License

This project is for portfolio and educational purposes.
