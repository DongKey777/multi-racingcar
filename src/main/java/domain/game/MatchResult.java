package domain.game;

public class MatchResult {
    private final boolean matched;
    private final int waitingCount;
    private final Players players;

    private MatchResult(boolean matched, int waitingCount, Players players) {
        this.matched = matched;
        this.waitingCount = waitingCount;
        this.players = players;
    }

    public static MatchResult matched(Players players) {
        return new MatchResult(true, Players.MAX_PLAYERS, players);
    }

    public static MatchResult waiting(int count) {
        return new MatchResult(false, count, null);
    }

    public boolean isMatched() {
        return matched;
    }

    public int getWaitingCount() {
        return waitingCount;
    }

    public Players getPlayers() {
        if (!matched) {
            throw new IllegalStateException("매칭되지 않은 상태에서는 플레이어 목록을 가져올 수 없습니다");
        }
        return players;
    }
}
