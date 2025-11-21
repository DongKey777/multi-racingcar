package domain.game;

import domain.event.GameEventPublisher;
import domain.vo.Round;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SingleGameRoom {
    private static final int MAX_ROUNDS = 5;
    private static final String[] AI_NAMES = {"AI_1", "AI_2", "AI_3"};
    private static final int ROUND_INITIAL_DELAY_SECONDS = 1;
    private static final int ROUND_INTERVAL_SECONDS = 1;

    private final Players players;
    private final String userNickname;
    private final ScheduledExecutorService scheduler;
    private final GameEventPublisher eventPublisher;
    private Round round;
    private boolean gameStarted;

    public SingleGameRoom(String nickname, GameEventPublisher eventPublisher) {
        this.userNickname = nickname;

        String[] allPlayers = new String[Players.MAX_PLAYERS];
        allPlayers[0] = nickname;
        System.arraycopy(AI_NAMES, 0, allPlayers, 1, AI_NAMES.length);

        this.players = new Players(allPlayers);
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.eventPublisher = eventPublisher;
        this.round = new Round(0, MAX_ROUNDS);
        this.gameStarted = false;
    }

    public void start() {
        if (gameStarted) {
            throw new IllegalStateException("이미 게임이 시작되었습니다");
        }

        gameStarted = true;
        System.out.println("\n싱글 플레이 게임 시작!");

        eventPublisher.publish(userNickname, "\n게임 시작! (싱글 플레이 - AI 3명과 경쟁)\n");

        scheduler.scheduleAtFixedRate(() -> {
            playOneRound();
        }, ROUND_INITIAL_DELAY_SECONDS, ROUND_INTERVAL_SECONDS, TimeUnit.SECONDS);
    }

    private void playOneRound() {
        if (eventPublisher.hasSession(userNickname) && !eventPublisher.hasActiveSession(userNickname)) {
            System.out.println("[경고] 사용자 연결 끊김 - 싱글 게임 중단");
            endGameEarly();
            return;
        }

        if (round.isLast()) {
            endGame();
            return;
        }

        round = round.next();
        System.out.println("\n=== Round " + round.getCurrent() + " ===");

        eventPublisher.publish(userNickname, "\n=== Round " + round.getCurrent() + " ===");

        players.moveAll();
        printRoundResult();
    }

    private void endGameEarly() {
        if (!scheduler.isShutdown()) {
            scheduler.shutdownNow();
            System.out.println("싱글 게임 조기 종료 (연결 끊김)");
        }
    }

    private void printRoundResult() {
        StringBuilder result = new StringBuilder();

        for (Player player : players.getPlayers()) {
            String line = player.getNickname() + " : " + "-".repeat(player.getPosition());
            System.out.println(line);
            result.append(line).append("\n");
        }

        eventPublisher.publish(userNickname, result.toString());
    }

    private void endGame() {
        scheduler.shutdown();
        broadcastGameEnd();
        announceWinners();
    }

    private void broadcastGameEnd() {
        System.out.println("\n게임 종료!");
        eventPublisher.publish(userNickname, "\n게임 종료!");
    }

    private void announceWinners() {
        List<Player> winners = players.getWinners();
        String winnerMessage = formatWinnerMessage(winners);
        System.out.println(winnerMessage);
        eventPublisher.publish(userNickname, "최종 우승자: " + winnerMessage);
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

    public Players getPlayers() {
        return players;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public int getCurrentRound() {
        return round.getCurrent();
    }

    public boolean isGameStarted() {
        return gameStarted;
    }
}
