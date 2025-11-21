package domain.vo;

public class RoomId {
    private final int value;

    public RoomId(int value) {
        if (value <= 0) {
            throw new IllegalArgumentException("RoomId는 양수여야 합니다");
        }
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RoomId roomId = (RoomId) o;
        return value == roomId.value;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
