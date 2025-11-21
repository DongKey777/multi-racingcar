package service;

import domain.event.GameEventPublisher;
import domain.game.GameMode;
import domain.game.GameRoom;
import domain.game.GameRoomRepository;
import domain.game.MatchResult;
import domain.game.Player;
import domain.game.PlayerJoinResult;
import domain.game.Players;
import domain.game.RoomCleanupScheduler;
import domain.game.SingleGameRoom;
import domain.game.WaitingQueue;
import domain.vo.RoomId;
import infrastructure.websocket.SessionManager;
import infrastructure.websocket.WebSocketSession;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

public class GameService {
    private final GameRoomRepository roomRepository;
    private final WaitingQueue waitingQueue;
    private final SessionManager sessionManager;
    private final GameEventPublisher eventPublisher;
    private final RoomCleanupScheduler cleanupScheduler;
    private final AtomicInteger roomIdGenerator;

    public GameService(
            SessionManager sessionManager,
            GameEventPublisher eventPublisher) {
        this.roomRepository = new GameRoomRepository();
        this.waitingQueue = new WaitingQueue();
        this.sessionManager = sessionManager;
        this.eventPublisher = eventPublisher;
        this.cleanupScheduler = new RoomCleanupScheduler();
        this.roomIdGenerator = new AtomicInteger(1);
    }

    public synchronized PlayerJoinResult joinGame(String nickname, GameMode mode, Socket socket) throws Exception {
        if (mode == GameMode.SINGLE) {
            return startSinglePlayerGame(nickname, socket);
        }
        return joinMultiplayerQueue(nickname, socket);
    }

    private PlayerJoinResult startSinglePlayerGame(String nickname, Socket socket) throws Exception {
        try {
            WebSocketSession session = new WebSocketSession(socket, nickname);
            sessionManager.add(nickname, session);

            RoomId roomId = generateRoomId();
            SingleGameRoom room = new SingleGameRoom(nickname, eventPublisher);
            roomRepository.saveSingleRoom(roomId, room);

            System.out.println("\n싱글 게임룸 #" + roomId + " 생성!");
            System.out.println("참가자: " + nickname);

            room.start();
            scheduleRoomCleanup(roomId, true);

            return PlayerJoinResult.success(1, true);

        } catch (Exception e) {
            sessionManager.remove(nickname);
            throw e;
        }
    }

    private PlayerJoinResult joinMultiplayerQueue(String nickname, Socket socket) throws Exception {
        try {
            WebSocketSession session = new WebSocketSession(socket, nickname);
            sessionManager.add(nickname, session);

            MatchResult matchResult = waitingQueue.addPlayer(nickname);
            int waitingCount = matchResult.getWaitingCount();

            System.out.println("플레이어 입장: " + nickname +
                    " (대기: " + waitingCount + "/" + Players.MAX_PLAYERS + ")");

            if (matchResult.isMatched()) {
                startMultiplayerGame(matchResult.getPlayers());
                return PlayerJoinResult.success(Players.MAX_PLAYERS, true);
            }

            String welcomeMessage = "입장 성공! 대기 중... (" + waitingCount + "/" + Players.MAX_PLAYERS + ")";
            sessionManager.sendTo(nickname, welcomeMessage);

            return PlayerJoinResult.success(waitingCount, false);

        } catch (Exception e) {
            waitingQueue.removePlayer(nickname);
            sessionManager.remove(nickname);
            throw e;
        }
    }

    private void startMultiplayerGame(Players players) {
        RoomId roomId = generateRoomId();

        String[] nicknames = players.getPlayers().stream()
                .map(Player::getNickname)
                .toArray(String[]::new);

        GameRoom room = new GameRoom(nicknames, eventPublisher);
        roomRepository.saveMultiRoom(roomId, room);

        System.out.println("\n멀티 게임룸 #" + roomId + " 생성!");
        System.out.println("참가자: " + String.join(", ", nicknames));

        room.start();
        scheduleRoomCleanup(roomId, false);
    }

    private void scheduleRoomCleanup(RoomId roomId, boolean isSingleRoom) {
        cleanupScheduler.scheduleCleanup(() -> {
            if (isSingleRoom) {
                roomRepository.removeSingleRoom(roomId);
                System.out.println("싱글 게임룸 #" + roomId + " 정리 완료");
            } else {
                roomRepository.removeMultiRoom(roomId);
                System.out.println("멀티 게임룸 #" + roomId + " 정리 완료");
            }
        });
    }

    private RoomId generateRoomId() {
        return new RoomId(roomIdGenerator.getAndIncrement());
    }

    public void leaveGame(String nickname) {
        if (nickname == null) {
            return;
        }

        waitingQueue.removePlayer(nickname);
        sessionManager.remove(nickname);
    }

    public int getWaitingPlayerCount() {
        return waitingQueue.getWaitingCount();
    }

    public int getActiveRoomCount() {
        return roomRepository.getTotalRoomCount();
    }

    public void sendWelcomeMessage(String nickname, GameMode mode, PlayerJoinResult result) {
        String message = createWelcomeMessage(mode, result);
        sessionManager.sendTo(nickname, message);
    }

    private String createWelcomeMessage(GameMode mode, PlayerJoinResult result) {
        if (mode == GameMode.SINGLE) {
            return "입장 성공! 싱글 플레이 시작...";
        }
        return "입장 성공! 대기 중... (" + result.getWaitingCount() + "/" + Players.MAX_PLAYERS + ")";
    }

    public void printStats() {
        System.out.println("\n서버 통계");
        System.out.println("대기 중: " + waitingQueue.getWaitingCount() + "/" + Players.MAX_PLAYERS);
        System.out.println("진행 중인 게임: " + roomRepository.getTotalRoomCount() + "개");
        System.out.println("총 생성된 게임: " + (roomIdGenerator.get() - 1) + "개");
    }
}
