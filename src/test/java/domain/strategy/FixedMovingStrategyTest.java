package domain.strategy;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FixedMovingStrategyTest {

    @Test
    @DisplayName("고정된 패턴대로 움직임 결정")
    void shouldMoveWithFixedPattern() {
        boolean[] pattern = {true, false, true, true};
        FixedMovingStrategy strategy = new FixedMovingStrategy(pattern);

        assertTrue(strategy.shouldMove());
        assertFalse(strategy.shouldMove());
        assertTrue(strategy.shouldMove());
        assertTrue(strategy.shouldMove());
    }

    @Test
    @DisplayName("패턴이 끝나면 처음부터 반복")
    void repeatPatternWhenEnds() {
        boolean[] pattern = {true, false};
        FixedMovingStrategy strategy = new FixedMovingStrategy(pattern);

        assertTrue(strategy.shouldMove());
        assertFalse(strategy.shouldMove());

        assertTrue(strategy.shouldMove());
        assertFalse(strategy.shouldMove());
    }

    @Test
    @DisplayName("원본 배열이 변경되어도 영향받지 않음")
    void immutablePattern() {
        boolean[] pattern = {true, false};
        FixedMovingStrategy strategy = new FixedMovingStrategy(pattern);

        pattern[0] = false;

        assertTrue(strategy.shouldMove());
    }
}
