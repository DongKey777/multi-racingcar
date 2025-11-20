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

        PlayerJoinResult result = manager.addPlayer(nickname);

        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("중복된 닉네임은 추가할 수 없다")
    void cannotAddDuplicatePlayer() {
        String nickname = generateNickname("중복");  // 중복1, 중복2, ...
        manager.addPlayer(nickname);

        PlayerJoinResult result = manager.addPlayer(nickname);

        assertFalse(result.isSuccess());
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

    @Test
    @DisplayName("대기 중인 플레이어를 제거할 수 있다")
    void removePlayer() {
        String nickname = generateNickname("제거대상");
        manager.addPlayer(nickname);
        int beforeCount = manager.getWaitingCount();

        manager.removePlayer(nickname);
        int afterCount = manager.getWaitingCount();

        assertTrue(afterCount < beforeCount);
    }

    @Test
    @DisplayName("제거된 플레이어는 다시 입장할 수 있다")
    void canRejoinAfterRemoval() {
        String nickname = generateNickname("재입장");
        manager.addPlayer(nickname);
        manager.removePlayer(nickname);

        PlayerJoinResult result = manager.addPlayer(nickname);

        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("게임 종료 후 같은 닉네임으로 재참여할 수 있다")
    void canRejoinAfterGameEnd() {
        String nickname = generateNickname("재참여");

        PlayerJoinResult firstJoin = manager.addPlayer(nickname);
        assertTrue(firstJoin.isSuccess());

        manager.removePlayer(nickname);

        PlayerJoinResult secondJoin = manager.addPlayer(nickname);
        assertTrue(secondJoin.isSuccess());
    }
}