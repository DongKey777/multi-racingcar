package domain.vo;

import java.util.Objects;

public class Round {
    private static final int MIN_VALUE = 0;
    private static final int MIN_MAX_ROUND = 1;

    private final int current;
    private final int max;

    public Round(int current, int max) {
        validateCurrent(current);
        validateMax(max);
        validateCurrentNotExceedsMax(current, max);
        this.current = current;
        this.max = max;
    }

    private void validateCurrent(int current) {
        if (current < MIN_VALUE) {
            throw new IllegalArgumentException(
                    String.format("Current round cannot be negative (given: %d)", current)
            );
        }
    }

    private void validateMax(int max) {
        if (max < MIN_MAX_ROUND) {
            throw new IllegalArgumentException(
                    String.format("Max round must be at least %d (given: %d)", MIN_MAX_ROUND, max)
            );
        }
    }

    private void validateCurrentNotExceedsMax(int current, int max) {
        if (current > max) {
            throw new IllegalArgumentException(
                    String.format("Current round (%d) cannot exceed max round (%d)", current, max)
            );
        }
    }

    public Round next() {
        if (isLast()) {
            throw new IllegalStateException(
                    String.format("Cannot proceed beyond max round (%d)", max)
            );
        }
        return new Round(current + 1, max);
    }

    public boolean isLast() {
        return current == max;
    }

    public int getCurrent() {
        return current;
    }

    public int getMax() {
        return max;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Round round = (Round) o;
        return current == round.current && max == round.max;
    }

    @Override
    public int hashCode() {
        return Objects.hash(current, max);
    }

    @Override
    public String toString() {
        return current + "/" + max;
    }
}
