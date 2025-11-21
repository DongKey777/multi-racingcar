package domain.game;

public class PlayerJoinResult {
    private final boolean success;
    private final int waitingCount;
    private final boolean gameStarted;

    public PlayerJoinResult(boolean success, int waitingCount, boolean gameStarted) {
        this.success = success;
        this.waitingCount = waitingCount;
        this.gameStarted = gameStarted;
    }

    public static PlayerJoinResult success(int waitingCount, boolean gameStarted) {
        return new PlayerJoinResult(true, waitingCount, gameStarted);
    }

    public static PlayerJoinResult failure() {
        return new PlayerJoinResult(false, 0, false);
    }

    public boolean isSuccess() {
        return success;
    }

    public int getWaitingCount() {
        return waitingCount;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }
}