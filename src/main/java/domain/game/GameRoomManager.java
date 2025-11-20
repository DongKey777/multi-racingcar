package domain.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class GameRoomManager {
    private static GameRoomManager instance;
    private final Map<Integer, GameRoom> activeRooms;
    private final AtomicInteger roomIdCounter;
    private Players waitingPlayers;
    private int currentRoomId;

    private GameRoomManager() {
        this.activeRooms = new ConcurrentHashMap<>();
        this.roomIdCounter = new AtomicInteger(1);
        this.waitingPlayers = new Players();
        this.currentRoomId = 0;
    }

    public static synchronized GameRoomManager getInstance() {
        if (instance == null) {
            instance = new GameRoomManager();
        }
        return instance;
    }

    public synchronized PlayerJoinResult addPlayer(String nickname) {
        try {
            waitingPlayers.add(nickname);
            int waitingCount = waitingPlayers.size();
            boolean gameStarted = false;

            System.out.println("플레이어 입장: " + nickname +
                    " (대기: " + waitingCount + "/4)");

            if (waitingPlayers.isFull()) {
                createAndStartGame();
                gameStarted = true;
            }

            return new PlayerJoinResult(true, waitingCount, gameStarted);
        } catch (IllegalArgumentException e) {
            System.out.println("입장 실패: " + e.getMessage());
            return new PlayerJoinResult(false, 0, false);
        }
    }

    private void createAndStartGame() {
        int roomId = roomIdCounter.getAndIncrement();

        String[] nicknames = waitingPlayers.getPlayers().stream()
                .map(Player::getNickname)
                .toArray(String[]::new);

        GameRoom room = new GameRoom(nicknames);
        activeRooms.put(roomId, room);

        System.out.println("\n🎮 게임룸 #" + roomId + " 생성!");
        System.out.println("참가자: " + String.join(", ", nicknames));

        room.start();
        waitingPlayers = new Players();
        scheduleRoomCleanup(roomId, 10);
    }

    private void scheduleRoomCleanup(int roomId, int delaySeconds) {
        new Thread(() -> {
            try {
                Thread.sleep(delaySeconds * 1000);
                activeRooms.remove(roomId);
                System.out.println("🗑️ 게임룸 #" + roomId + " 정리 완료");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public int getWaitingCount() {
        return waitingPlayers.size();
    }

    public int getActiveRoomCount() {
        return activeRooms.size();
    }

    public List<GameRoom> getActiveRooms() {
        return new ArrayList<>(activeRooms.values());
    }

    public synchronized void removePlayer(String nickname) {
        waitingPlayers.remove(nickname);
        System.out.println("플레이어 제거: " + nickname +
                " (대기: " + waitingPlayers.size() + "/4)");
    }

    public void printStats() {
        System.out.println("\n📊 서버 통계");
        System.out.println("대기 중: " + waitingPlayers.size() + "/4");
        System.out.println("진행 중인 게임: " + activeRooms.size() + "개");
        System.out.println("총 생성된 게임: " + (roomIdCounter.get() - 1) + "개");
    }
}