package controller;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import domain.game.GameMode;
import domain.game.MatchResult;
import domain.game.PlayerJoinResult;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.GameRoomService;
import service.MatchingService;
import service.PlayerSessionService;

class GameControllerTest {

    private GameController controller;
    private PlayerSessionService sessionService;
    private MatchingService matchingService;
    private GameRoomService roomService;

    @BeforeEach
    void setUp() {
        sessionService = mock(PlayerSessionService.class);
        matchingService = mock(MatchingService.class);
        roomService = mock(GameRoomService.class);
        controller = new GameController(sessionService, matchingService, roomService);
    }

    @Test
    @DisplayName("싱글 게임 입장을 처리할 수 있다")
    void attemptJoinSingleGame() throws Exception {
        Socket socket = mock(Socket.class);
        OutputStream out = new ByteArrayOutputStream();
        when(socket.getOutputStream()).thenReturn(out);

        PlayerJoinResult result = controller.attemptJoinGame("player1", GameMode.SINGLE, socket);

        assertTrue(result.isSuccess());
        verify(sessionService).createSession("player1", socket);
        verify(roomService).createAndStartSingleRoom("player1");
    }

    @Test
    @DisplayName("멀티 게임 입장을 처리할 수 있다")
    void attemptJoinMultiGame() throws Exception {
        Socket socket = mock(Socket.class);
        OutputStream out = new ByteArrayOutputStream();
        when(socket.getOutputStream()).thenReturn(out);

        MatchResult matchResult = MatchResult.waiting(1);
        when(matchingService.joinQueue("player1")).thenReturn(matchResult);

        PlayerJoinResult result = controller.attemptJoinGame("player1", GameMode.MULTI, socket);

        assertTrue(result.isSuccess());
        assertFalse(result.isGameStarted());
        verify(sessionService).createSession("player1", socket);
        verify(matchingService).joinQueue("player1");
    }

    @Test
    @DisplayName("플레이어 퇴장을 처리할 수 있다")
    void handlePlayerLeave() {
        controller.handlePlayerLeave("player1");

        verify(matchingService).leaveQueue("player1");
        verify(sessionService).closeSession("player1");
    }
}
