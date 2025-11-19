package domain.game;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GameRoomManagerTest {

    private GameRoomManager manager;
    private static int testCounter = 0;

    @BeforeEach
    void setUp() {
        manager = GameRoomManager.getInstance();
        testCounter++;
    }

    private String generateNickname(String prefix) {
        return prefix + testCounter;
    }

    @Test
    @DisplayName("싱글톤 인스턴스를 반환한다")
    void singleton() {
        GameRoomManager another = GameRoomManager.getInstance();

        assertSame(manager, another);
    }

    @Test
    @DisplayName("플레이어를 추가할 수 있다")
    void addPlayer() {
        String nickname = generateNickname("플레이어");  // 플레이어1, 플레이어2, ...

        boolean result = manager.addPlayer(nickname);

        assertTrue(result);
    }

    @Test
    @DisplayName("중복된 닉네임은 추가할 수 없다")
    void cannotAddDuplicatePlayer() {
        String nickname = generateNickname("중복");  // 중복1, 중복2, ...
        manager.addPlayer(nickname);

        boolean result = manager.addPlayer(nickname);

        assertFalse(result);
    }

    @Test
    @DisplayName("대기 중인 플레이어 수를 확인할 수 있다")
    void getWaitingCount() {
        int count = manager.getWaitingCount();

        assertTrue(count >= 0 && count <= 4);
    }

    @Test
    @DisplayName("진행 중인 게임 수를 확인할 수 있다")
    void getActiveRoomCount() {
        int count = manager.getActiveRoomCount();

        assertTrue(count >= 0);
    }
}