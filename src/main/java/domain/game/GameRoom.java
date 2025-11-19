package domain.game;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GameRoom {
    private static final int MAX_ROUNDS = 5;

    private final Players players;
    private final ScheduledExecutorService scheduler;
    private int currentRound;
    private boolean gameStarted;

    public GameRoom(String[] nicknames) {
        this.players = new Players(nicknames);
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.currentRound = 0;
        this.gameStarted = false;
    }

    public boolean addPlayer(String nickname) {
        if (players.isFull()) {
            return false;
        }

        if (isGameStarted()) {
            return false;
        }

        try {
            players.add(nickname);
            System.out.println("플레이어 입장: " + nickname +
                    " (" + players.size() + "/4)");

            if (players.isFull()) {
                start();
            }

            return true;
        } catch (IllegalArgumentException e) {
            System.out.println("입장 실패: " + e.getMessage());
            return false;
        }
    }

    public void start() {
        if (gameStarted) {
            throw new IllegalStateException("이미 게임이 시작되었습니다");
        }

        gameStarted = true;
        System.out.println("\n게임 시작!");

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

        players.moveAll();
        printRoundResult();
    }

    private void printRoundResult() {
        for (Player player : players.getPlayers()) {
            System.out.println(player.getNickname() + " : " + "-".repeat(player.getPosition()));
        }
    }

    private void endGame() {
        scheduler.shutdown();

        System.out.println("\n게임 종료!");
        System.out.print("최종 우승자 : ");

        List<Player> winners = players.getWinners();
        for (int i = 0; i < winners.size(); i++) {
            System.out.print(winners.get(i).getNickname());
            if (i < winners.size() - 1) {
                System.out.print(", ");
            }
        }
        System.out.println();
    }

    public Players getPlayers() {
        return players;
    }

    public boolean isFull() {
        return players.isFull();
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }
}