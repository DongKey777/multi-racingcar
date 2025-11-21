package controller;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import domain.game.GameMode;
import domain.game.PlayerJoinResult;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.GameService;

class GameControllerTest {

    private GameController controller;
    private GameService gameService;

    @BeforeEach
    void setUp() {
        gameService = mock(GameService.class);
        controller = new GameController(gameService);
    }

    @Test
    @DisplayName("플레이어 입장을 처리할 수 있다")
    void attemptJoinGame() throws Exception {
        Socket socket = mock(Socket.class);
        OutputStream out = new ByteArrayOutputStream();
        when(socket.getOutputStream()).thenReturn(out);

        PlayerJoinResult expectedResult = new PlayerJoinResult(true, 1, false);
        when(gameService.joinGame("player1", GameMode.MULTI, socket)).thenReturn(expectedResult);

        PlayerJoinResult result = controller.attemptJoinGame("player1", GameMode.MULTI, socket);

        assertTrue(result.isSuccess());
        verify(gameService).joinGame("player1", GameMode.MULTI, socket);
        verify(gameService).sendWelcomeMessage("player1", GameMode.MULTI, expectedResult);
    }

    @Test
    @DisplayName("플레이어 입장 실패를 처리할 수 있다")
    void attemptJoinGameFailure() throws Exception {
        Socket socket = mock(Socket.class);
        OutputStream out = new ByteArrayOutputStream();
        when(socket.getOutputStream()).thenReturn(out);

        PlayerJoinResult expectedResult = new PlayerJoinResult(false, 0, false);
        when(gameService.joinGame("duplicate", GameMode.MULTI, socket)).thenReturn(expectedResult);

        PlayerJoinResult result = controller.attemptJoinGame("duplicate", GameMode.MULTI, socket);

        assertFalse(result.isSuccess());
        verify(gameService).joinGame("duplicate", GameMode.MULTI, socket);
    }

    @Test
    @DisplayName("플레이어 퇴장을 처리할 수 있다")
    void handlePlayerLeave() {
        controller.handlePlayerLeave("player1");

        verify(gameService).leaveGame("player1");
    }
}
