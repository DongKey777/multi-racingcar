package domain.game;

import infrastructure.websocket.SessionManager;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SingleGameRoom {
    private static final int MAX_ROUNDS = 5;

    private final Player player;
    private final ScheduledExecutorService scheduler;
    private final SessionManager sessionManager;
    private int currentRound;
    private boolean gameStarted;

    public SingleGameRoom(String nickname) {
        this.player = new Player(nickname);
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.sessionManager = SessionManager.getInstance();
        this.currentRound = 0;
        this.gameStarted = false;
    }

    public void start() {
        if (gameStarted) {
            throw new IllegalStateException("이미 게임이 시작되었습니다");
        }

        gameStarted = true;
        System.out.println("\n싱글 플레이 게임 시작!");

        sessionManager.sendTo(player.getNickname(), "\n게임 시작! (싱글 플레이)\n");

        scheduler.scheduleAtFixedRate(() -> {
            playOneRound();
        }, 1, 1, TimeUnit.SECONDS);
    }

    private void playOneRound() {
        if (currentRound >= MAX_ROUNDS) {
            endGame();
            return;
        }

        currentRound++;
        System.out.println("\n=== Round " + currentRound + " ===");

        sessionManager.sendTo(player.getNickname(), "\n=== Round " + currentRound + " ===");

        moveRandomly();
        printRoundResult();
    }

    private void moveRandomly() {
        int random = (int) (Math.random() * 2);
        if (random == 1) {
            player.moveForward();
        }
    }

    private void printRoundResult() {
        String line = player.getNickname() + " : " + "-".repeat(player.getPosition());
        System.out.println(line);
        sessionManager.sendTo(player.getNickname(), line);
    }

    private void endGame() {
        scheduler.shutdown();

        System.out.println("\n게임 종료!");
        sessionManager.sendTo(player.getNickname(), "\n게임 종료!");

        String winnerMessage = "🏆 최종 위치: " + player.getPosition() + "칸";
        System.out.println(winnerMessage);
        sessionManager.sendTo(player.getNickname(), winnerMessage);
    }

    public Player getPlayer() {
        return player;
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }
}
