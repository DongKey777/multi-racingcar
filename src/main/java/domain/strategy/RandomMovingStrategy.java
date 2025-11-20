package domain.strategy;

public class RandomMovingStrategy implements MovingStrategy {
    private static final double MOVE_THRESHOLD = 0.5;

    @Override
    public boolean shouldMove() {
        return Math.random() >= MOVE_THRESHOLD;
    }
}
