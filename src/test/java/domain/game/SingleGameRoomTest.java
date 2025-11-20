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
    void getPlayer() {
        String nickname = "동훈";
        SingleGameRoom room = new SingleGameRoom(nickname);

        Player player = room.getPlayer();

        assertNotNull(player);
        assertEquals(nickname, player.getNickname());
    }
}
