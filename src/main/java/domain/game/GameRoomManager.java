package domain.game;

public class GameRoomManager {
    private static GameRoomManager instance;
    private Players waitingPlayers;
    private GameRoom currentRoom;

    private GameRoomManager() {
        this.waitingPlayers = new Players();
        this.currentRoom = null;
    }

    public static synchronized GameRoomManager getInstance() {
        if (instance == null) {
            instance = new GameRoomManager();
        }
        return instance;
    }

    public synchronized boolean addPlayer(String nickname) {
        if (currentRoom != null && currentRoom.isGameStarted()) {
            System.out.println("게임 진행 중입니다.");
            return false;
        }

        try {
            waitingPlayers.add(nickname);
            System.out.println("플레이어 입장: " + nickname +
                    " (" + waitingPlayers.size() + "/4)");

            if (waitingPlayers.isFull()) {
                startGame();
            }

            return true;
        } catch (IllegalArgumentException e) {
            System.out.println("입장 실패: " + e.getMessage());
            return false;
        }
    }

    private void startGame() {
        String[] nicknames = waitingPlayers.getPlayers().stream()
                .map(Player::getNickname)
                .toArray(String[]::new);

        currentRoom = new GameRoom(nicknames);
        currentRoom.start();

        waitingPlayers = new Players();
    }

    public GameRoom getCurrentRoom() {
        return currentRoom;
    }

    public int getWaitingCount() {
        return waitingPlayers.size();
    }
}