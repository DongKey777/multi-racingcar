package domain.game;

public class WaitingQueue {
    private Players players;

    public WaitingQueue() {
        this.players = new Players();
    }

    public MatchResult addPlayer(String nickname) {
        players.add(nickname);

        if (players.isFull()) {
            Players matched = players;
            players = new Players();
            return MatchResult.matched(matched);
        }

        return MatchResult.waiting(players.size());
    }

    public void removePlayer(String nickname) {
        players.remove(nickname);
    }

    public int getWaitingCount() {
        return players.size();
    }

    public Players getWaitingPlayers() {
        return players;
    }
}
