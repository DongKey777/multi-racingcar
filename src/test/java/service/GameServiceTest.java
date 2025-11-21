package service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import domain.event.GameEventPublisher;
import domain.game.GameMode;
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
    private SessionManager sessionManager;
    private GameEventPublisher eventPublisher;

    @BeforeEach
    void setUp() {
        sessionManager = mock(SessionManager.class);
        eventPublisher = mock(GameEventPublisher.class);
        gameService = new GameService(sessionManager, eventPublisher);
    }

    @Test
    @DisplayName("플레이어를 게임에 참여시킬 수 있다")
    void joinGame_Success() throws Exception {
        Socket socket = mock(Socket.class);
        OutputStream out = new ByteArrayOutputStream();
        when(socket.getOutputStream()).thenReturn(out);

        PlayerJoinResult result = gameService.joinGame("김철수", GameMode.SINGLE, socket);

        assertTrue(result.isSuccess());
        verify(sessionManager).add(eq("김철수"), any());
    }

    @Test
    @DisplayName("플레이어가 게임을 떠날 때 정리된다")
    void leaveGame() {
        gameService.leaveGame("김철수");

        verify(sessionManager).remove("김철수");
    }

    @Test
    @DisplayName("대기 중인 플레이어 수를 조회할 수 있다")
    void getWaitingPlayerCount() {
        int count = gameService.getWaitingPlayerCount();

        assertEquals(0, count);
    }

    @Test
    @DisplayName("활성 게임룸 수를 조회할 수 있다")
    void getActiveRoomCount() {
        int count = gameService.getActiveRoomCount();

        assertEquals(0, count);
    }

}

