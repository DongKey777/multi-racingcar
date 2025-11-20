package domain.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SingleGameRoomTest {

    @Test
    @DisplayName("싱글 게임룸을 생성할 수 있다")
    void createSingleGameRoom() {
        String nickname = "동훈";

        SingleGameRoom room = new SingleGameRoom(nickname);

        assertNotNull(room);
        assertEquals(0, room.getCurrentRound());
        assertFalse(room.isGameStarted());
        assertEquals(4, room.getPlayers().size()); // 사용자 1명 + AI 3명
    }

    @Test
    @DisplayName("싱글 게임을 시작할 수 있다")
    void startSingleGame() throws InterruptedException {
        String nickname = "동훈";
        SingleGameRoom room = new SingleGameRoom(nickname);

        room.start();
        Thread.sleep(100);

        assertTrue(room.isGameStarted());
    }

    @Test
    @DisplayName("이미 시작된 게임은 다시 시작할 수 없다")
    void cannotStartGameTwice() throws InterruptedException {
        String nickname = "동훈";
        SingleGameRoom room = new SingleGameRoom(nickname);
        room.start();
        Thread.sleep(100);

        assertThrows(IllegalStateException.class, () -> {
            room.start();
        });
    }

    @Test
    @DisplayName("라운드가 진행된다")
    void roundProgresses() throws InterruptedException {
        String nickname = "동훈";
        SingleGameRoom room = new SingleGameRoom(nickname);

        room.start();
        Thread.sleep(1500);

        assertTrue(room.getCurrentRound() >= 1);
    }

    @Test
    @DisplayName("5라운드 후 게임이 종료된다")
    void gameEndsAfterFiveRounds() throws InterruptedException {
        String nickname = "동훈";
        SingleGameRoom room = new SingleGameRoom(nickname);

        room.start();
        Thread.sleep(7000);

        assertEquals(5, room.getCurrentRound());
    }

    @Test
    @DisplayName("플레이어 정보를 조회할 수 있다")
    void getPlayers() {
        String nickname = "동훈";
        SingleGameRoom room = new SingleGameRoom(nickname);

        Players players = room.getPlayers();

        assertNotNull(players);
        assertEquals(4, players.size());
        assertEquals(nickname, room.getUserNickname());
    }

    @Test
    @DisplayName("사용자와 AI 플레이어가 모두 포함된다")
    void includesUserAndAI() {
        String nickname = "동훈";
        SingleGameRoom room = new SingleGameRoom(nickname);

        Players players = room.getPlayers();
        boolean hasUser = players.getPlayers().stream()
                .anyMatch(p -> p.getNickname().equals(nickname));

        assertTrue(hasUser);
        assertEquals(4, players.size());
    }
}
