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
import infrastructure.websocket.session.SessionManager;
import infrastructure.websocket.session.WebSocketSession;
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
        boolean sessionRegistered = false;

        try {
            WebSocketSession session = new WebSocketSession(socket, nickname);
            sessionManager.add(nickname, session);
            sessionRegistered = true;

            RoomId roomId = generateRoomId();
            SingleGameRoom room = new SingleGameRoom(nickname, eventPublisher);
            roomRepository.saveSingleRoom(roomId, room);

            System.out.println("\n싱글 게임룸 #" + roomId + " 생성!");
            System.out.println("참가자: " + nickname);

            String welcomeMessage = "입장 성공! 싱글 플레이 시작...";
            sessionManager.sendTo(nickname, welcomeMessage);

            room.start();
            scheduleRoomCleanup(roomId, true, new String[]{nickname});

            return PlayerJoinResult.success(1, true);

        } catch (Exception e) {
            if (sessionRegistered) {
                sessionManager.remove(nickname);
            }
            throw e;
        }
    }

    private PlayerJoinResult joinMultiplayerQueue(String nickname, Socket socket) throws Exception {
        boolean sessionRegistered = false;
        boolean queueAdded = false;

        try {
            WebSocketSession session = new WebSocketSession(socket, nickname);
            sessionManager.add(nickname, session);
            sessionRegistered = true;

            MatchResult matchResult = waitingQueue.addPlayer(nickname);
            queueAdded = true;
            int waitingCount = matchResult.getWaitingCount();

            System.out.println("플레이어 입장: " + nickname +
                    " (대기: " + waitingCount + "/" + Players.MAX_PLAYERS + ")");

            if (matchResult.isMatched()) {
                startMultiplayerGame(matchResult.getPlayers());
                return PlayerJoinResult.success(Players.MAX_PLAYERS, true);
            }

            String welcomeMessage = "입장 성공! 대기 중... (" + waitingCount + "/" + Players.MAX_PLAYERS + ")";
            sessionManager.sendTo(nickname, welcomeMessage);

            notifyWaitingPlayersExcept(nickname, waitingCount);

            return PlayerJoinResult.success(waitingCount, false);

        } catch (Exception e) {
            if (queueAdded) {
                waitingQueue.removePlayer(nickname);
            }
            if (sessionRegistered) {
                sessionManager.remove(nickname);
            }
            throw e;
        }
    }

    private void notifyWaitingPlayersExcept(String excludeNickname, int currentCount) {
        Players waitingPlayers = waitingQueue.getWaitingPlayers();
        String updateMessage = "대기 중... (" + currentCount + "/" + Players.MAX_PLAYERS + ")";

        for (Player player : waitingPlayers.getPlayers()) {
            String playerNickname = player.getNickname();
            if (!playerNickname.equals(excludeNickname)) {
                sessionManager.sendTo(playerNickname, updateMessage);
            }
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
        scheduleRoomCleanup(roomId, false, nicknames);
    }

    private void scheduleRoomCleanup(RoomId roomId, boolean isSingleRoom, String[] nicknames) {
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

    public void printStats() {
        System.out.println("\n서버 통계");
        System.out.println("대기 중: " + waitingQueue.getWaitingCount() + "/" + Players.MAX_PLAYERS);
        System.out.println("진행 중인 게임: " + roomRepository.getTotalRoomCount() + "개");
        System.out.println("활성 세션: " + sessionManager.getActiveSessionCount() + "개");
        System.out.println("총 생성된 게임: " + (roomIdGenerator.get() - 1) + "개");
    }
}
