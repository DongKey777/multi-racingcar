package domain.game;

import domain.vo.Nickname;
import domain.vo.Position;

public class Player {
    private static final int MAX_NICKNAME_LENGTH = 10;

    private final Nickname nickname;
    private Position position;

    public Player(String nickname) {
        validateNicknameLength(nickname);
        this.nickname = new Nickname(nickname);
        this.position = new Position(0);
    }

    public int getPosition() {
        return position.getValue();
    }

    public String getNickname() {
        return nickname.getValue();
    }

    public void moveForward() {
        position = position.moveForward();
    }

    private void validateNicknameLength(String nickname) {
        if (nickname == null || nickname.isBlank()) {
            throw new IllegalArgumentException("닉네임은 비어있을 수 없습니다");
        }
        if (nickname.length() > MAX_NICKNAME_LENGTH) {
            throw new IllegalArgumentException("닉네임은 10글자 이하여야 합니다");
        }
    }

    @Override
    public String toString() {
        return nickname + ": " + position + "칸";
    }
}