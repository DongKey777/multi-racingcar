package domain.game;

import domain.vo.Nickname;
import domain.vo.Position;

public class Player {
    private final Nickname nickname;
    private Position position;

    public Player(String nickname) {
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

    @Override
    public String toString() {
        return nickname + ": " + position + "칸";
    }
}