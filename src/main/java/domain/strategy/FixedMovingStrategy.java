package domain.strategy;

public class FixedMovingStrategy implements MovingStrategy {
    private final boolean[] moves;
    private int currentIndex;

    public FixedMovingStrategy(boolean[] moves) {
        this.moves = moves.clone();
        this.currentIndex = 0;
    }

    @Override
    public boolean shouldMove() {
        if (currentIndex >= moves.length) {
            currentIndex = 0;
        }
        return moves[currentIndex++];
    }
}
