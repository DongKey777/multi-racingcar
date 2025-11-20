package domain.game;

import domain.vo.Round;
import infrastructure.websocket.SessionManager;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GameRoom {
    private static final int MAX_ROUNDS = 5;

    private final Players players;
    private final ScheduledExecutorService scheduler;
    private final SessionManager sessionManager;
    private Round round;
    private boolean gameStarted;

    public GameRoom(String[] nicknames) {
        this.players = new Players(nicknames);
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.sessionManager = SessionManager.getInstance();
        this.round = new Round(0, MAX_ROUNDS);
        this.gameStarted = false;
    }

    public void start() {
        if (gameStarted) {
            throw new IllegalStateException("이미 게임이 시작되었습니다");
        }

        gameStarted = true;
        System.out.println("\n게임 시작!");

        broadcastToPlayers("\n게임 시작!\n");

        scheduler.scheduleAtFixedRate(() -> {
            playOneRound();
        }, 1, 1, TimeUnit.SECONDS);
    }

    private void playOneRound() {
        if (round.isLast()) {
            endGame();
            return;
        }

        round = round.next();
        System.out.println("\n=== Round " + round.getCurrent() + " ===");

        broadcastToPlayers("\n=== Round " + round.getCurrent() + " ===");

        players.moveAll();
        printRoundResult();
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
        scheduler.shutdown();
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
            if (!sessionManager.hasSession(nickname)) {
                continue;
            }

            anySessionExists = true;
            if (sessionManager.hasActiveSession(nickname)) {
                sessionManager.sendTo(nickname, message);
                connectedCount++;
            }
        }

        checkAndTerminateIfAllDisconnected(anySessionExists, connectedCount);
    }

    private void checkAndTerminateIfAllDisconnected(boolean anySessionExists, int connectedCount) {
        if (anySessionExists && connectedCount == 0 && gameStarted) {
            System.out.println("[경고] 모든 플레이어 연결 끊김 - 게임 중단");
            endGameEarly();
        }
    }

    private void endGameEarly() {
        if (!scheduler.isShutdown()) {
            scheduler.shutdownNow();
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
}