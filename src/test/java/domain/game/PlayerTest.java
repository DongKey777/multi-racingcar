package domain.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PlayerTest {

    @Test
    @DisplayName("플레이어를 생성할 수 있다")
    void createPlayer() {
        Player player = new Player("동훈");

        assertEquals("동훈", player.getNickname());
        assertEquals(0, player.getPosition());
    }

    @Test
    @DisplayName("닉네임이 null이면 예외가 발생한다")
    void createPlayerWithNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Player(null);
        });
    }

    @Test
    @DisplayName("닉네임이 빈 문자열이면 예외가 발생한다")
    void createPlayerWithBlank() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Player("");
        });
    }

    @Test
    @DisplayName("닉네임이 공백만 있으면 예외가 발생한다")
    void createPlayerWithWhitespace() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Player("   ");
        });
    }

    @Test
    @DisplayName("닉네임이 10글자를 초과하면 예외가 발생한다")
    void createPlayerWithLongNickname() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Player("12345678901");
        });
    }

    @Test
    @DisplayName("플레이어가 전진할 수 있다")
    void moveForward() {
        Player player = new Player("동훈");

        player.moveForward();

        assertEquals(1, player.getPosition());
    }

    @Test
    @DisplayName("플레이어가 여러 번 전진할 수 있다")
    void moveForwardMultipleTimes() {
        Player player = new Player("동훈");

        player.moveForward();
        player.moveForward();
        player.moveForward();

        assertEquals(3, player.getPosition());
    }

    @Test
    @DisplayName("toString은 닉네임과 위치를 반환한다")
    void testToString() {
        Player player = new Player("동훈");
        player.moveForward();
        player.moveForward();

        String result = player.toString();

        assertEquals("동훈: 2칸", result);
    }
}