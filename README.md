# Racing Car From Scratch

프레임워크 없이 Java로 만드는 실시간 4인 멀티플레이 레이싱 게임

---

## 기능 목록

### HTTP 서버

- [x] ServerSocket 8080 포트 바인딩
- [x] HTTP 요청 파싱
- [x] HTTP 응답 생성
- [x] 정적 파일 제공 (HTML, CSS, JS)
- [x] MIME Type 자동 설정
- [x] 멀티스레드 처리 (클라이언트별 독립 스레드)

### WebSocket 서버

- [x] Handshake 처리
- [x] Frame 파싱
- [x] Frame 전송
- [x] WebSocketSession 세션 관리
- [x] SessionManager 중앙 관리
- [x] 브로드캐스트 기능
- [x] 개별 메시지 전송
- [ ] Extended Length 지원
- [ ] Ping/Pong, Close Frame 처리

### 게임 로직

#### 도메인 (domain/game/)

- [x] Player: 닉네임, 위치, 이동
- [x] Players: 일급 컬렉션, 4명 관리, 중복 검증
- [x] GameRoom: 게임 진행, 5라운드, 우승자 결정
- [x] GameRoomManager: 싱글톤, 다중 게임룸 관리

#### 게임 진행

- [x] 닉네임 입력 및 플레이어 생성
- [x] 4명 자동 매칭
- [x] 대기열 관리 (Players)
- [x] 게임 자동 시작 (4명 모이면)
- [x] 게임 진행 (1초 간격, 5라운드)
- [x] 랜덤 이동 (0 or 1칸)
- [x] 우승자 결정 (최대 거리)
- [x] 게임 종료 후 자동 정리 (10초 후)
- [x] 다중 게임룸 동시 진행
-

#### 실시간 통신

- [x] 게임 시작 알림 브로드캐스트
- [x] 라운드별 진행 상황 브로드캐스트
- [x] 라운드 결과 브로드캐스트
- [x] 게임 종료 및 우승자 브로드캐스트
- [x] 모든 플레이어 동일 화면 확인

### 동시성 제어

- [x] 멀티스레드 (클라이언트별 독립 처리)
- [x] ConcurrentHashMap (세션 관리)
- [x] ConcurrentHashMap (게임룸 관리)
- [x] AtomicInteger (게임룸 ID)
- [x] synchronized (플레이어 추가)
- [ ] 연결 끊김 시 대기열 자동 제거
- [ ] 게임 종료 시 세션 자동 정리
- [ ] 비정상 종료 처리

### 프론트엔드

- [x] 터미널 스타일 UI (HTML/CSS)
- [x] WebSocket 연결
- [x] 닉네임 입력 (한글 지원)
- [x] 입장 성공/실패 메시지
- [x] 실시간 게임 진행 화면
- [x] 라운드별 결과 표시
- [x] 우승자 표시
- [ ] 브라우저 캐시 방지
- [ ] 재연결 기능
- [ ] 에러 메시지 UI