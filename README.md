# 🏎️ Racing Car From Scratch

프레임워크 없이 Java로 만드는 실시간 4인 멀티플레이 레이싱 게임

---

## 📋 기능 목록

### HTTP 서버

- [ ] ServerSocket으로 8080 포트 서버 실행
- [ ] HTTP 요청 파싱 (Request Line, Headers)
- [ ] HTTP 응답 생성 (Status Line, Headers, Body)
- [ ] 정적 파일 제공 (HTML, CSS, JS)

### WebSocket 서버

- [ ] WebSocket Handshake 처리
- [ ] WebSocket Frame 파싱
- [ ] WebSocket Frame 생성
- [ ] 클라이언트 브로드캐스트

### 게임 로직

- [ ] 플레이어 입장 (닉네임 입력)
- [ ] 4명 매칭
- [ ] 게임 자동 시작 및 진행 (1초 간격, 5라운드)
- [ ] 우승자 결정 및 게임 초기화

### 동시성 제어

- [ ] Thread Pool 적용
- [ ] Race Condition 방지 (synchronized, ConcurrentHashMap)
- [ ] 연결 끊김 처리

### 프론트엔드

- [ ] 게임 화면 (HTML/CSS/JS)
- [ ] WebSocket 통신 연결

### 배포

- [ ] AWS EC2 배포