package domain.game;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GameRoomManagerTest {

    private GameRoomManager manager;

    @BeforeEach
    void setUp() {
        manager = GameRoomManager.getInstance();
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
        String uniqueNickname = "테스트" + UUID.randomUUID().toString().substring(0, 8);

        boolean result = manager.addPlayer(uniqueNickname);

        assertTrue(result);
    }

    @Test
    @DisplayName("중복된 닉네임은 추가할 수 없다")
    void cannotAddDuplicatePlayer() {
        String uniqueNickname = "중복테스트" + UUID.randomUUID().toString().substring(0, 8);
        manager.addPlayer(uniqueNickname);

        boolean result = manager.addPlayer(uniqueNickname);

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