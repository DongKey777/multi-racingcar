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

        System.out.println("\n게임 종료!");
        broadcastToPlayers("\n게임 종료!");

        List<Player> winners = players.getWinners();
        StringBuilder winnerMessage = new StringBuilder("🏆 최종 우승자: ");

        for (int i = 0; i < winners.size(); i++) {
            String name = winners.get(i).getNickname();
            System.out.print(name);
            winnerMessage.append(name);

            if (i < winners.size() - 1) {
                System.out.print(", ");
                winnerMessage.append(", ");
            }
        }
        System.out.println();

        broadcastToPlayers(winnerMessage.toString());
    }

    private void broadcastToPlayers(String message) {
        for (Player player : players.getPlayers()) {
            sessionManager.sendTo(player.getNickname(), message);
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