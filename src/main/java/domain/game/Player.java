package domain.game;

public class Player {
    private final String nickname;
    private int position;
    private static final int MAX_NICKNAME_LENGTH = 10;

    public Player(String nickname) {
        validateNicknameLength(nickname);
        this.nickname = nickname;
        this.position = 0;
    }

    public int getPosition() {
        return position;
    }

    public String getNickname() {
        return nickname;
    }

    public void moveForward() {
        position += 1;
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