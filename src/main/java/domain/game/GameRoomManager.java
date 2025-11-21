package domain.game;

import domain.event.GameEventPublisher;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class GameRoomManager {
    private final RoomRegistry roomRegistry;
    private final AtomicInteger roomIdCounter;
    private final GameEventPublisher eventPublisher;
    private Players waitingPlayers;

    public GameRoomManager(GameEventPublisher eventPublisher) {
        this.roomRegistry = new RoomRegistry();
        this.roomIdCounter = new AtomicInteger(1);
        this.eventPublisher = eventPublisher;
        this.waitingPlayers = new Players();
    }

    public synchronized PlayerJoinResult addPlayer(String nickname, GameMode mode) {
        if (mode == GameMode.SINGLE) {
            return createAndStartSingleGame(nickname);
        }

        return addToMultiplayerQueue(nickname);
    }

    private PlayerJoinResult addToMultiplayerQueue(String nickname) {
        try {
            waitingPlayers.add(nickname);
            int waitingCount = waitingPlayers.size();

            System.out.println("플레이어 입장: " + nickname +
                    " (대기: " + waitingCount + "/4)");

            boolean gameStarted = tryStartMultiGame();

            return new PlayerJoinResult(true, waitingCount, gameStarted);
        } catch (IllegalArgumentException e) {
            System.out.println("입장 실패: " + e.getMessage());
            return new PlayerJoinResult(false, 0, false);
        }
    }

    private boolean tryStartMultiGame() {
        if (!waitingPlayers.isFull()) {
            return false;
        }

        createAndStartMultiGame();
        return true;
    }

    public synchronized PlayerJoinResult addPlayer(String nickname) {
        return addPlayer(nickname, GameMode.MULTI);
    }

    private PlayerJoinResult createAndStartSingleGame(String nickname) {
        int roomId = roomIdCounter.getAndIncrement();

        SingleGameRoom room = new SingleGameRoom(nickname, eventPublisher);
        roomRegistry.addSingleRoom(roomId, room);

        System.out.println("\n싱글 게임룸 #" + roomId + " 생성!");
        System.out.println("참가자: " + nickname);

        room.start();
        scheduleSingleRoomCleanup(roomId, 10);

        return new PlayerJoinResult(true, 1, true);
    }

    private void createAndStartMultiGame() {
        int roomId = roomIdCounter.getAndIncrement();

        String[] nicknames = waitingPlayers.getPlayers().stream()
                .map(Player::getNickname)
                .toArray(String[]::new);

        GameRoom room = new GameRoom(nicknames, eventPublisher);
        roomRegistry.addMultiRoom(roomId, room);

        System.out.println("\n멀티 게임룸 #" + roomId + " 생성!");
        System.out.println("참가자: " + String.join(", ", nicknames));

        room.start();
        waitingPlayers = new Players();
        scheduleMultiRoomCleanup(roomId, 10);
    }

    private void scheduleSingleRoomCleanup(int roomId, int delaySeconds) {
        new Thread(() -> {
            try {
                Thread.sleep(delaySeconds * 1000);
                roomRegistry.removeSingleRoom(roomId);
                System.out.println("싱글 게임룸 #" + roomId + " 정리 완료");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void scheduleMultiRoomCleanup(int roomId, int delaySeconds) {
        new Thread(() -> {
            try {
                Thread.sleep(delaySeconds * 1000);
                roomRegistry.removeMultiRoom(roomId);
                System.out.println("멀티 게임룸 #" + roomId + " 정리 완료");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public int getWaitingCount() {
        return waitingPlayers.size();
    }

    public int getActiveRoomCount() {
        return roomRegistry.getTotalRoomCount();
    }

    public List<GameRoom> getActiveRooms() {
        return roomRegistry.getMultiRooms();
    }

    public List<SingleGameRoom> getActiveSingleRooms() {
        return roomRegistry.getSingleRooms();
    }

    public synchronized void removePlayer(String nickname) {
        waitingPlayers.remove(nickname);
        System.out.println("플레이어 제거: " + nickname +
                " (대기: " + waitingPlayers.size() + "/4)");
    }

    public void printStats() {
        System.out.println("\n서버 통계");
        System.out.println("대기 중: " + waitingPlayers.size() + "/4");
        System.out.println("진행 중인 게임: " + roomRegistry.getTotalRoomCount() + "개");
        System.out.println("총 생성된 게임: " + (roomIdCounter.get() - 1) + "개");
    }
}