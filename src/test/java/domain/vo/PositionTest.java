package domain.vo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PositionTest {

    @Test
    @DisplayName("유효한 위치를 생성할 수 있다")
    void createValidPosition() {
        Position position = new Position(5);

        assertEquals(5, position.getValue());
    }

    @Test
    @DisplayName("0 위치를 생성할 수 있다")
    void createZeroPosition() {
        Position position = new Position(0);

        assertEquals(0, position.getValue());
    }

    @Test
    @DisplayName("음수 위치는 생성할 수 없다")
    void negativePositionThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Position(-1);
        });
    }

    @Test
    @DisplayName("위치를 1 증가시킬 수 있다")
    void moveForward() {
        Position position = new Position(3);

        Position moved = position.moveForward();

        assertEquals(4, moved.getValue());
        assertEquals(3, position.getValue());
    }

    @Test
    @DisplayName("같은 값을 가진 위치는 동일하다")
    void equalsSameValue() {
        Position position1 = new Position(5);
        Position position2 = new Position(5);

        assertEquals(position1, position2);
        assertEquals(position1.hashCode(), position2.hashCode());
    }

    @Test
    @DisplayName("다른 값을 가진 위치는 동일하지 않다")
    void notEqualsDifferentValue() {
        Position position1 = new Position(5);
        Position position2 = new Position(3);

        assertNotEquals(position1, position2);
    }

    @Test
    @DisplayName("위치를 비교할 수 있다")
    void comparePositions() {
        Position smaller = new Position(3);
        Position larger = new Position(5);

        assertTrue(smaller.compareTo(larger) < 0);
        assertTrue(larger.compareTo(smaller) > 0);
        assertEquals(0, smaller.compareTo(new Position(3)));
    }

    @Test
    @DisplayName("toString은 위치 값을 반환한다")
    void toStringReturnsValue() {
        Position position = new Position(5);

        assertEquals("5", position.toString());
    }
}
