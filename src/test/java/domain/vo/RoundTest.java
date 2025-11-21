package domain.vo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RoundTest {

    @Test
    @DisplayName("유효한 라운드를 생성할 수 있다")
    void createValidRound() {
        Round round = new Round(3, 5);

        assertEquals(3, round.getCurrent());
        assertEquals(5, round.getMax());
    }

    @Test
    @DisplayName("0번째 라운드를 생성할 수 있다")
    void createZeroRound() {
        Round round = new Round(0, 5);

        assertEquals(0, round.getCurrent());
    }

    @Test
    @DisplayName("현재 라운드가 음수면 예외가 발생한다")
    void negativeCurrentRoundThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Round(-1, 5);
        });
    }

    @Test
    @DisplayName("최대 라운드가 0 이하면 예외가 발생한다")
    void zeroOrNegativeMaxRoundThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Round(0, 0);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new Round(0, -1);
        });
    }

    @Test
    @DisplayName("현재 라운드가 최대 라운드를 초과하면 예외가 발생한다")
    void currentExceedsMaxThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Round(6, 5);
        });
    }

    @Test
    @DisplayName("라운드를 1 증가시킬 수 있다")
    void nextRound() {
        Round round = new Round(3, 5);

        Round next = round.next();

        assertEquals(4, next.getCurrent());
        assertEquals(5, next.getMax());
        assertEquals(3, round.getCurrent());
    }

    @Test
    @DisplayName("마지막 라운드에서 증가시키면 예외가 발생한다")
    void nextRoundAtMaxThrowsException() {
        Round round = new Round(5, 5);

        assertThrows(IllegalStateException.class, () -> {
            round.next();
        });
    }

    @Test
    @DisplayName("마지막 라운드인지 확인할 수 있다")
    void isLastRound() {
        Round notLast = new Round(4, 5);
        Round last = new Round(5, 5);

        assertFalse(notLast.isLast());
        assertTrue(last.isLast());
    }

    @Test
    @DisplayName("같은 값을 가진 라운드는 동일하다")
    void equalsSameValue() {
        Round round1 = new Round(3, 5);
        Round round2 = new Round(3, 5);

        assertEquals(round1, round2);
        assertEquals(round1.hashCode(), round2.hashCode());
    }

    @Test
    @DisplayName("다른 값을 가진 라운드는 동일하지 않다")
    void notEqualsDifferentValue() {
        Round round1 = new Round(3, 5);
        Round round2 = new Round(4, 5);
        Round round3 = new Round(3, 10);

        assertNotEquals(round1, round2);
        assertNotEquals(round1, round3);
    }

    @Test
    @DisplayName("toString은 '현재/최대' 형식을 반환한다")
    void toStringReturnsFormattedValue() {
        Round round = new Round(3, 5);

        assertEquals("3/5", round.toString());
    }
}
