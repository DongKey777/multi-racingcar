package service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.contains;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import domain.event.GameEventPublisher;
import domain.game.GameMode;
import domain.game.GameRoomManager;
import domain.game.PlayerJoinResult;
import infrastructure.websocket.SessionManager;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GameServiceTest {

    private GameService gameService;
    private GameRoomManager gameRoomManager;
    private SessionManager sessionManager;
    private GameEventPublisher eventPublisher;

    @BeforeEach
    void setUp() {
        gameRoomManager = mock(GameRoomManager.class);
        sessionManager = mock(SessionManager.class);
        eventPublisher = mock(GameEventPublisher.class);
        gameService = new GameService(gameRoomManager, sessionManager, eventPublisher);
    }

    @Test
    @DisplayName("플레이어를 게임에 참여시킬 수 있다")
    void joinGame_Success() throws Exception {
        Socket socket = mock(Socket.class);
        OutputStream out = new ByteArrayOutputStream();
        when(socket.getOutputStream()).thenReturn(out);

        PlayerJoinResult expectedResult = new PlayerJoinResult(true, 1, false);
        when(gameRoomManager.addPlayer("player1", GameMode.MULTI)).thenReturn(expectedResult);

        PlayerJoinResult result = gameService.joinGame("player1", GameMode.MULTI, socket);

        assertTrue(result.isSuccess());
        verify(gameRoomManager).addPlayer("player1", GameMode.MULTI);
        verify(sessionManager).add(eq("player1"), any());
    }

    @Test
    @DisplayName("게임 참여 실패 시 세션을 등록하지 않는다")
    void joinGame_Failure() throws Exception {
        Socket socket = mock(Socket.class);
        OutputStream out = new ByteArrayOutputStream();
        when(socket.getOutputStream()).thenReturn(out);

        PlayerJoinResult expectedResult = new PlayerJoinResult(false, 0, false);
        when(gameRoomManager.addPlayer("duplicate", GameMode.MULTI)).thenReturn(expectedResult);

        PlayerJoinResult result = gameService.joinGame("duplicate", GameMode.MULTI, socket);

        assertFalse(result.isSuccess());
        verify(gameRoomManager).addPlayer("duplicate", GameMode.MULTI);
        verify(sessionManager, never()).add(anyString(), any());
    }

    @Test
    @DisplayName("플레이어가 게임을 떠날 때 정리된다")
    void leaveGame() {
        gameService.leaveGame("player1");

        verify(gameRoomManager).removePlayer("player1");
        verify(sessionManager).remove("player1");
    }

    @Test
    @DisplayName("대기 중인 플레이어 수를 조회할 수 있다")
    void getWaitingPlayerCount() {
        when(gameRoomManager.getWaitingCount()).thenReturn(3);

        int count = gameService.getWaitingPlayerCount();

        assertEquals(3, count);
        verify(gameRoomManager).getWaitingCount();
    }

    @Test
    @DisplayName("활성 게임룸 수를 조회할 수 있다")
    void getActiveRoomCount() {
        when(gameRoomManager.getActiveRoomCount()).thenReturn(2);

        int count = gameService.getActiveRoomCount();

        assertEquals(2, count);
        verify(gameRoomManager).getActiveRoomCount();
    }

    @Test
    @DisplayName("환영 메시지를 전송할 수 있다")
    void sendWelcomeMessage() {
        PlayerJoinResult result = new PlayerJoinResult(true, 2, false);

        gameService.sendWelcomeMessage("player1", GameMode.MULTI, result);

        verify(sessionManager).sendTo(eq("player1"), contains("대기 중"));
    }
}
