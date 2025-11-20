package domain.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PlayersTest {

    private Players players;

    @BeforeEach
    void setUp() {
        players = new Players();
    }

    @Test
    @DisplayName("플레이어를 동적으로 추가할 수 있다")
    void addPlayer() {
        players.add("동훈");

        assertEquals(1, players.size());
    }

    @Test
    @DisplayName("4명까지 플레이어를 추가할 수 있다")
    void addFourPlayers() {
        players.add("동훈");
        players.add("철수");
        players.add("영희");
        players.add("민수");

        assertEquals(4, players.size());
        assertTrue(players.isFull());
    }

    @Test
    @DisplayName("중복된 닉네임은 추가할 수 없다")
    void addDuplicateNickname() {
        players.add("동훈");

        assertThrows(IllegalArgumentException.class, () -> {
            players.add("동훈");
        });
    }

    @Test
    @DisplayName("4명이 넘으면 추가할 수 없다")
    void addMoreThanFourPlayers() {
        players.add("동훈");
        players.add("철수");
        players.add("영희");
        players.add("민수");

        assertThrows(IllegalStateException.class, () -> {
            players.add("하니");
        });
    }

    @Test
    @DisplayName("배열로 4명을 한번에 생성할 수 있다")
    void createPlayersWithArray() {
        String[] nicknames = {"동훈", "철수", "영희", "민수"};

        Players players = new Players(nicknames);

        assertEquals(4, players.size());
        assertTrue(players.isFull());
    }

    @Test
    @DisplayName("배열 생성 시 중복된 닉네임이 있으면 예외가 발생한다")
    void createPlayersWithDuplicateArray() {
        String[] nicknames = {"동훈", "철수", "동훈", "민수"};

        assertThrows(IllegalArgumentException.class, () -> {
            new Players(nicknames);
        });
    }

    @Test
    @DisplayName("배열 생성 시 4명이 아니면 예외가 발생한다")
    void createPlayersWithInvalidCount() {
        String[] nicknames = {"동훈", "철수", "영희"};

        assertThrows(IllegalArgumentException.class, () -> {
            new Players(nicknames);
        });
    }

    @Test
    @DisplayName("모든 플레이어가 이동할 수 있다")
    void moveAll() {
        players.add("동훈");
        players.add("철수");
        players.add("영희");
        players.add("민수");

        players.moveAll();

        List<Player> playerList = players.getPlayers();
        for (Player player : playerList) {
            assertTrue(player.getPosition() >= 0);
            assertTrue(player.getPosition() <= 1);
        }
    }

    @Test
    @DisplayName("우승자를 찾을 수 있다")
    void getWinners() {
        players.add("동훈");
        players.add("철수");
        players.add("영희");
        players.add("민수");

        for (int i = 0; i < 10; i++) {
            players.moveAll();
        }

        List<Player> winners = players.getWinners();

        assertFalse(winners.isEmpty());

        int winnerPosition = winners.get(0).getPosition();
        for (Player winner : winners) {
            assertEquals(winnerPosition, winner.getPosition());
        }

        List<Player> allPlayers = players.getPlayers();
        for (Player player : allPlayers) {
            assertTrue(player.getPosition() <= winnerPosition);
        }
    }

    @Test
    @DisplayName("플레이어 목록을 불변으로 반환한다")
    void getPlayersReturnsImmutableList() {
        players.add("동훈");
        players.add("철수");

        List<Player> playerList = players.getPlayers();

        assertEquals(2, playerList.size());

        playerList.clear();
        assertEquals(2, players.size());
    }

    @Test
    @DisplayName("특정 닉네임의 플레이어를 제거할 수 있다")
    void removePlayer() {
        players.add("동훈");
        players.add("철수");
        players.add("영희");

        players.remove("철수");

        assertEquals(2, players.size());
        assertFalse(players.isFull());
    }

    @Test
    @DisplayName("제거된 플레이어는 다시 추가할 수 있다")
    void addPlayerAfterRemoval() {
        players.add("동훈");
        players.remove("동훈");

        players.add("동훈");

        assertEquals(1, players.size());
    }

    @Test
    @DisplayName("존재하지 않는 플레이어를 제거해도 예외가 발생하지 않는다")
    void removeNonExistentPlayer() {
        players.add("동훈");

        players.remove("철수");

        assertEquals(1, players.size());
    }
}