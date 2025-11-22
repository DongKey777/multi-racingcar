# Multi Racing Car Game

프레임워크 없이 Java로 만드는 실시간 4인 멀티플레이 레이싱 게임
우아한테크코스 8기 오픈미션 프로젝트

---

## 게임 화면

**메인 화면**

<img width="400" height="500" alt="스크린샷 2025-11-22 오후 11 38 16" src="https://github.com/user-attachments/assets/f52c6327-8d39-4f63-8f19-cdee3a0b5ed0" />


**모드 선택**

<img width="400" height="500" alt="스크린샷 2025-11-22 오후 11 38 57" src="https://github.com/user-attachments/assets/06aa341c-7975-48e8-ab55-96b1f99c785d" />


**대기실 (멀티플레이)**

<img width="400" height="500" alt="스크린샷 2025-11-22 오후 11 39 11" src="https://github.com/user-attachments/assets/43793970-d597-4701-a444-814ad4c9d962" />


**게임 플레이**

<img width="400" height="800" alt="스크린샷 2025-11-22 오후 11 39 49" src="https://github.com/user-attachments/assets/73b608be-7a79-4b70-b710-c48927c79fb5" />

---

## 프로젝트 소개

### 게임 개요

Multi Racing Car는 최대 4명이 동시에 접속하여 5라운드 동안 경주하는 실시간 멀티플레이어 게임입니다. 플레이어는 닉네임을 입력하고 대기열에 참가하며, 4명이 모이면 자동으로 게임이 시작됩니다.

**🎮 게임 플레이: https://multi-racing-car.com**

### 게임 모드

#### 멀티플레이어

- 최대 4명의 실제 플레이어
- 4명이 모이면 자동 시작
- 대기열 시스템
- 실시간 진행 상황 공유

#### 싱글플레이어

- 1명 + AI 3명 (AI_1, AI_2, AI_3)
- 대기 없이 즉시 시작
- AI도 동일한 확률로 이동

### 핵심 메커니즘

- **실시간 멀티플레이**: WebSocket 기반 4인 동시 접속 및 브로드캐스트
- **랜덤 레이싱**: 매 라운드 50% 확률로 0~1칸 무작위 이동
- **자동 매칭**: 4명이 모이면 게임 자동 시작
- **자동 정리**: 게임 종료 10초 후 게임룸 자동 정리

### 게임 규칙

1. 닉네임 입력 (2~6자, 중복 불가)
2. 게임 모드 선택 (Single / Multiplayer)
3. 멀티플레이어: 4명 대기 → 자동 시작
4. 싱글플레이어: 즉시 시작
5. 5라운드 자동 진행 (1초 간격)
6. 가장 멀리 이동한 플레이어 우승 (공동 우승 가능)

---

## 게임 플레이 방법

### 멀티플레이

1. 브라우저 창을 4개 열기
2. 각각 다른 닉네임 입력 (2~6자)
3. "Multiplayer" 선택
4. 4명이 모이면 자동으로 게임 시작
5. 5라운드 자동 진행 (1초 간격)
6. 우승자 발표 후 "Restart" 버튼으로 재시작

### 싱글플레이

1. 브라우저 1개만 열기
2. 닉네임 입력
3. "Single Player" 선택
4. AI 3명(AI_1, AI_2, AI_3)과 즉시 게임 시작

---

## 오픈미션: 낯선 도구 해커톤

### 미션 선정

**"프레임워크 없이 HTTP/WebSocket 서버 직접 구현"**

기초가 부족했던 저에게 HTTP 서버를 직접 구현하는 것은 새로운 도전이었습니다. 이번 프로젝트는 "낯선 도구 해커톤" 방향으로, 프레임워크 없이 네트워크 프로토콜부터 게임
로직까지 직접 구현하는 것을 목표로 했습니다.

### 개발 전략: 단계적 확장

#### Phase 1 (1주차): HTTP 서버 + 콘솔 게임

- ServerSocket 기반 HTTP 서버 구현
- 정적 파일 제공 (HTML/CSS/JS)
- 게임 도메인 로직 완성
- 콘솔 환경에서 게임 검증

#### Phase 2 (2주차): WebSocket 실시간 통신

- RFC 6455 기반 WebSocket Handshake 구현
- Frame 파싱 및 전송 로직 (비트 연산)
- 세션 관리 시스템 (SessionManager)
- 브로드캐스트 및 개별 메시지 전송

#### Phase 3 (3주차): 아키텍처 개선

- God Object(Service) 안티패턴 해결 → 3개 서비스로 분리
- 도메인 순수성 확보 (인프라 의존성 제거)
- 버그 수정 (세션 관리, 대기열, 닉네임 중복, 메모리 누수)
- 테스트 코드 작성

### 왜 프레임워크 없이 구현했나?

#### 1. 네트워크 동작 원리 이해

Spring Boot를 쓴다면 `@RestController`만 써도 HTTP 통신이 됩니다. 하지만 **어떻게** 작동하는 지 이해하고 싶었습니다.

- HTTP 요청은 어떻게 파싱되는가?
- WebSocket Handshake는 어떤 과정을 거치는가?
- 세션은 어떻게 관리되는가?
- 바이트 스트림은 어떻게 처리하는가?

이런 질문들에 답하기 위해, 직접 구현 해보기로 했습니다.

**학습한 내용:**

- ServerSocket의 accept() 블로킹 메커니즘
- InputStream/OutputStream 바이트 단위 처리
- WebSocket Frame의 마스킹(XOR) 알고리즘
- SHA-1 해싱 + Base64 인코딩 (Handshake)
- 비트 연산으로 Opcode, Payload Length 추출

**직접 구현한 것들:**

- HTTP 요청 파싱 (`GET /index.html HTTP/1.1`)
- MIME Type 자동 설정 (`.css` → `text/css`)
- WebSocket Handshake (Sec-WebSocket-Key + Magic String)
- Frame 인코딩/디코딩 (마스킹, 페이로드 추출)
- JSON 파싱 (외부 라이브러리 없이 문자열 처리)
- 세션 관리 (ConcurrentHashMap)
- 동시성 제어

이 과정에서 **왜 프레임워크가 필요한지**, **어떤 문제를 해결해주는지** 체감할 수 있었습니다.

#### 2. 아키텍처 설계와 동시성 제어

프레임워크의 구조 없이, 스스로 레이어를 나누고 책임을 분리해야 했습니다.

**레이어 구조:**

```
Controller (GameController)
    ↓ 조율
Service Layer (3개 서비스)
    ├─ PlayerSessionService (세션 생성/종료)
    ├─ MatchingService (대기열/매칭)
    └─ GameRoomService (게임룸 생성/스케줄링)
    ↓ 도메인 로직 호출
Domain Layer (순수 Java, 인프라 독립)
    ├─ game/ (Player, GameRoom, WaitingQueue 등)
    ├─ vo/ (Nickname, Position, Round, RoomId)
    ├─ strategy/ (MovingStrategy)
    └─ event/ (GameEventPublisher 인터페이스)
    ↓ 추상화 의존
Infrastructure Layer (구현체)
    ├─ http/ (ServerSocket 기반 HTTP 서버)
    ├─ websocket/ (RFC 6455 WebSocket 구현)
    └─ scheduler/ (게임룸 정리 스케줄러)
```

**적용한 설계 원칙:**

- 단일 책임 원칙 (SRP): 서비스를 3개로 분리
- 의존성 역전 원칙 (DIP): GameEventPublisher 인터페이스로 추상화
- 개방-폐쇄 원칙 (OCP): MovingStrategy로 확장 가능
- Value Object 패턴: Primitive Obsession 회피
- First-Class Collection: Players 일급 컬렉션

**동시성 제어:**

멀티플레이어 게임 특성상 여러 클라이언트가 동시에 접속하면서 다양한 동시성 문제를 경험했습니다.

- 닉네임 중복 검증 (race condition)
- 대기열 추가/제거 (thread-safety)
- 게임룸 생성/삭제 (리소스 관리)
- 세션 동시 접근 문제

이를 해결하기 위해 ConcurrentHashMap, AtomicInteger, ScheduledExecutorService 등을 활용했습니다.

이 경험을 통해 "프레임워크에 맞춰 코드를 짜는 것"과 "구조를 스스로 설계하는 것"의 차이를 배웠습니다.

---

## 기술 스택

### Core

- **Language**: Java 21
- **Build Tool**: Gradle 8.14
- **Runtime**: JVM (OpenJDK 21)

### Network (직접 구현)

- **HTTP Server**: ServerSocket 기반 구현
- **WebSocket**: RFC 6455 기반 구현 (Handshake, Frame 파싱)
- **Protocol**: HTTP/1.1, WebSocket Text Frame

### 테스트

- **Framework**: JUnit 5 (Jupiter)
- **Mocking**: Mockito 5.5.0

### 프론트엔드

- **UI**: HTML/CSS/JavaScript

---

## 프로젝트 구조

```
src/main/java/
├── controller/           # GameController
├── service/              # PlayerSessionService, MatchingService, GameRoomService
├── domain/
│   ├── game/            # Player, GameRoom, WaitingQueue 등
│   ├── vo/              # Nickname, Position, Round, RoomId
│   ├── strategy/        # MovingStrategy (전략 패턴)
│   └── event/           # GameEventPublisher (DIP)
└── infrastructure/
    ├── http/            # ServerSocket 기반 HTTP 서버
    ├── websocket/       # RFC 6455 WebSocket 구현
    └── scheduler/       # 게임룸 정리 스케줄러

public/
├── index.html
├── css/terminal.css
└── js/                  # GameApp, WebSocketClient 등 6개 파일

src/test/java/           
```

---

## 개발 성과

이 프로젝트를 통해 가장 크게 얻은 성과는 **프레임워크 없이 네트워크 서버를 직접 구현하는 경험**입니다.

**Spring Boot를 쓸 때는 몰랐던 것들:**

- HTTP 요청이 어떻게 바이트 스트림으로 들어오는지
- WebSocket Handshake에서 SHA-1 + Base64가 왜 필요한지
- 세션을 어떻게 Thread-safe하게 관리해야 하는지
- 동시성 문제가 왜 발생하는지 (race condition)
- 소켓을 언제 닫아야 하는지 (리소스 관리)

직접 구현하면서, **프레임워크의 추상화**을 이해하게 되었습니다.
