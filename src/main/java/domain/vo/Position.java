package domain.vo;

import java.util.Objects;

public class Position implements Comparable<Position> {
    private static final int MIN_VALUE = 0;

    private final int value;

    public Position(int value) {
        validateNonNegative(value);
        this.value = value;
    }

    private void validateNonNegative(int value) {
        if (value < MIN_VALUE) {
            throw new IllegalArgumentException(
                    String.format("Position cannot be negative (given: %d)", value)
            );
        }
    }

    public Position moveForward() {
        return new Position(value + 1);
    }

    public int getValue() {
        return value;
    }

    @Override
    public int compareTo(Position other) {
        return Integer.compare(this.value, other.value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Position position = (Position) o;
        return value == position.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
