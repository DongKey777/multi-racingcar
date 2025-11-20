package domain.vo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class NicknameTest {

    @Test
    @DisplayName("유효한 닉네임을 생성할 수 있다")
    void createValidNickname() {
        Nickname nickname = new Nickname("홍길동");

        assertEquals("홍길동", nickname.getValue());
    }

    @Test
    @DisplayName("2글자 닉네임을 생성할 수 있다")
    void createTwoCharacterNickname() {
        Nickname nickname = new Nickname("동훈");

        assertEquals("동훈", nickname.getValue());
    }

    @Test
    @DisplayName("6글자 닉네임을 생성할 수 있다")
    void createSixCharacterNickname() {
        Nickname nickname = new Nickname("abcdef");

        assertEquals("abcdef", nickname.getValue());
    }

    @Test
    @DisplayName("빈 문자열은 닉네임으로 사용할 수 없다")
    void emptyNicknameThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Nickname("");
        });
    }

    @Test
    @DisplayName("null은 닉네임으로 사용할 수 없다")
    void nullNicknameThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Nickname(null);
        });
    }

    @Test
    @DisplayName("공백만 있는 문자열은 닉네임으로 사용할 수 없다")
    void blankNicknameThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Nickname("   ");
        });
    }

    @Test
    @DisplayName("1글자 닉네임은 사용할 수 없다")
    void oneCharacterNicknameThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Nickname("a");
        });
    }

    @Test
    @DisplayName("7글자 이상 닉네임은 사용할 수 없다")
    void sevenCharacterNicknameThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Nickname("abcdefg");
        });
    }

    @Test
    @DisplayName("같은 값을 가진 닉네임은 동일하다")
    void equalsSameValue() {
        Nickname nickname1 = new Nickname("동훈");
        Nickname nickname2 = new Nickname("동훈");

        assertEquals(nickname1, nickname2);
        assertEquals(nickname1.hashCode(), nickname2.hashCode());
    }

    @Test
    @DisplayName("다른 값을 가진 닉네임은 동일하지 않다")
    void notEqualsDifferentValue() {
        Nickname nickname1 = new Nickname("동훈");
        Nickname nickname2 = new Nickname("철수");

        assertNotEquals(nickname1, nickname2);
    }

    @Test
    @DisplayName("toString은 닉네임 값을 반환한다")
    void toStringReturnsValue() {
        Nickname nickname = new Nickname("동훈");

        assertEquals("동훈", nickname.toString());
    }
}
