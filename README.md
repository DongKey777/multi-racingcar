# 🏎️ Racing Car From Scratch

프레임워크 없이 Java로 만드는 실시간 4인 멀티플레이 레이싱 게임

---

## 📋 기능 목록

### HTTP 서버

- [x] ServerSocket 8080 포트 바인딩
- [x] HTTP 요청 파싱 (Method, Path, Headers)
- [x] HTTP 응답 생성 (200 OK, 404 Not Found)
- [x] 정적 파일 제공 (HTML, CSS, JS)
- [x] MIME Type 자동 설정

### WebSocket 서버

- [x] Handshake 처리 (Upgrade, Sec-WebSocket-Key/Accept)
- [x] Frame 파싱 (FIN, Opcode, Mask, Payload)
- [x] Frame 전송 (Text, 125바이트 이하)
- [ ] Extended Length 지원 (126, 127)
- [ ] Ping/Pong, Close Frame 처리

### 게임 로직

- [x] WebSocket 연결
- [ ] 닉네임 입력 및 플레이어 생성
- [ ] 4명 매칭 및 대기실
- [ ] 게임 자동 진행 (1초 간격, 5라운드)
- [ ] 랜덤 이동 (0 or 1)
- [ ] 우승자 결정 및 게임 초기화

### 동시성 제어

- [x] Thread Pool (CachedThreadPool)
- [ ] ConcurrentHashMap (플레이어 관리)
- [ ] synchronized (게임 상태)
- [ ] 연결 끊김 처리

### 프론트엔드

- [x] 터미널 스타일 UI
- [x] WebSocket 연결
- [ ] JSON 메시지 송수신
- [ ] 게임 화면 (대기/진행/결과)