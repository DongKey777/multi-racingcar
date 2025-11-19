package domain.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Players {
    private static final int MAX_PLAYERS = 4;
    private final List<Player> players = new ArrayList<>();

    public Players(String[] nicknames) {
        validateDuplicatedNickname(nicknames);
        validatePlayerCount(nicknames);
        Arrays.stream(nicknames).forEach(name -> players.add(new Player(name)));
    }

    private void validateDuplicatedNickname(String[] nicknames) {
        if (Arrays.stream(nicknames).distinct().count() != nicknames.length) {
            throw new IllegalArgumentException("중복된 닉네임이 있습니다");
        }
    }

    private void validatePlayerCount(String[] nicknames) {
        if (nicknames.length != MAX_PLAYERS) {
            throw new IllegalArgumentException("플레이어는 정확히 4명이어야 합니다");
        }
    }

    public void moveAll() {
        for (Player player : players) {
            moveRandomly(player);
        }
    }

    private void moveRandomly(Player player) {
        int random = (int) (Math.random() * 2);  // 0 or 1
        if (random == 1) {
            player.moveForward();
        }
    }

    public List<Player> getWinners() {
        int maxPosition = players.stream()
                .mapToInt(Player::getPosition)
                .max()
                .orElse(0);

        return players.stream()
                .filter(player -> player.getPosition() == maxPosition)
                .toList();
    }

    public List<Player> getPlayers() {
        return new ArrayList<>(players);
    }

    public int size() {
        return players.size();
    }
}