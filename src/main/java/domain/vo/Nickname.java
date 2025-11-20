package domain.vo;

import java.util.Objects;

public class Nickname {
    private static final int MIN_LENGTH = 2;
    private static final int MAX_LENGTH = 6;

    private final String value;

    public Nickname(String value) {
        validateNotNull(value);
        validateNotBlank(value);
        validateLength(value);
        this.value = value;
    }

    private void validateNotNull(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Nickname cannot be null");
        }
    }

    private void validateNotBlank(String value) {
        if (value.trim().isEmpty()) {
            throw new IllegalArgumentException("Nickname cannot be empty");
        }
    }

    private void validateLength(String value) {
        int length = value.length();
        if (length < MIN_LENGTH || length > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("Nickname must be between %d and %d characters", MIN_LENGTH, MAX_LENGTH)
            );
        }
    }

    public String getValue() {
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
        Nickname nickname = (Nickname) o;
        return Objects.equals(value, nickname.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
