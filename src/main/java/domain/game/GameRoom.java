package domain.game;

import domain.event.GameEventPublisher;
import domain.vo.Round;
import java.util.List;

public class GameRoom {
    private static final int MAX_ROUNDS = 5;

    private final Players players;
    private final GameEventPublisher eventPublisher;
    private Round round;
    private boolean gameStarted;
    private boolean gameEnded;

    public GameRoom(String[] nicknames, GameEventPublisher eventPublisher) {
        this.players = new Players(nicknames);
        this.eventPublisher = eventPublisher;
        this.round = new Round(0, MAX_ROUNDS);
        this.gameStarted = false;
        this.gameEnded = false;
    }

    public void start() {
        if (gameStarted) {
            throw new IllegalStateException("이미 게임이 시작되었습니다");
        }

        gameStarted = true;
        System.out.println("\n게임 시작!");
        broadcastToPlayers("\n게임 시작!\n");
    }

    public boolean playNextRound() {
        if (gameEnded) {
            return false;
        }

        if (round.isLast()) {
            endGame();
            return false;
        }

        round = round.next();
        System.out.println("\n=== Round " + round.getCurrent() + " ===");
        broadcastToPlayers("\n=== Round " + round.getCurrent() + " ===");

        players.moveAll();
        printRoundResult();
        return true;
    }

    private void printRoundResult() {
        StringBuilder result = new StringBuilder();

        for (Player player : players.getPlayers()) {
            String line = player.getNickname() + " : " + "-".repeat(player.getPosition());
            System.out.println(line);
            result.append(line).append("\n");
        }

        broadcastToPlayers(result.toString());
    }

    private void endGame() {
        gameEnded = true;
        broadcastGameEnd();
        announceWinners();
    }

    private void broadcastGameEnd() {
        System.out.println("\n게임 종료!");
        broadcastToPlayers("\n게임 종료!");
    }

    private void announceWinners() {
        List<Player> winners = players.getWinners();
        String winnerMessage = formatWinnerMessage(winners);
        System.out.println(winnerMessage);
        broadcastToPlayers("최종 우승자: " + winnerMessage);
    }

    private String formatWinnerMessage(List<Player> winners) {
        StringBuilder message = new StringBuilder();
        for (int i = 0; i < winners.size(); i++) {
            message.append(winners.get(i).getNickname());
            if (i < winners.size() - 1) {
                message.append(", ");
            }
        }
        return message.toString();
    }

    private void broadcastToPlayers(String message) {
        int connectedCount = 0;
        boolean anySessionExists = false;

        for (Player player : players.getPlayers()) {
            String nickname = player.getNickname();
            if (!eventPublisher.hasSession(nickname)) {
                continue;
            }

            anySessionExists = true;
            if (eventPublisher.hasActiveSession(nickname)) {
                eventPublisher.publish(nickname, message);
                connectedCount++;
            }
        }

        checkAndTerminateIfAllDisconnected(anySessionExists, connectedCount);
    }

    private void checkAndTerminateIfAllDisconnected(boolean anySessionExists, int connectedCount) {
        if (anySessionExists && connectedCount == 0 && gameStarted && !gameEnded) {
            System.out.println("[경고] 모든 플레이어 연결 끊김 - 게임 중단");
            gameEnded = true;
            System.out.println("게임 조기 종료 (연결 끊김)");
        }
    }

    public Players getPlayers() {
        return players;
    }

    public int getCurrentRound() {
        return round.getCurrent();
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public boolean isGameEnded() {
        return gameEnded;
    }
}