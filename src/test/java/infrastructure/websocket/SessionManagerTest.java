package infrastructure.websocket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import infrastructure.websocket.session.SessionManager;
import infrastructure.websocket.session.WebSocketSession;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SessionManagerTest {

    private SessionManager manager;

    @BeforeEach
    void setUp() {
        manager = new SessionManager();
    }

    @Test
    @DisplayName("세션을 등록할 수 있다")
    void addSession() throws IOException {
        Socket socket = mock(Socket.class);
        OutputStream out = new ByteArrayOutputStream();
        when(socket.getOutputStream()).thenReturn(out);
        WebSocketSession session = new WebSocketSession(socket, "테스트1");

        manager.add("테스트1", session);

        assertTrue(manager.hasSession("테스트1"));
    }

    @Test
    @DisplayName("세션을 제거할 수 있다")
    void removeSession() throws IOException {
        Socket socket = mock(Socket.class);
        OutputStream out = new ByteArrayOutputStream();
        when(socket.getOutputStream()).thenReturn(out);
        when(socket.isClosed()).thenReturn(false);
        WebSocketSession session = new WebSocketSession(socket, "테스트2");
        manager.add("테스트2", session);

        manager.remove("테스트2");

        assertFalse(manager.hasSession("테스트2"));
    }

    @Test
    @DisplayName("활성 세션 수를 확인할 수 있다")
    void getActiveSessionCount() throws IOException {
        int initialCount = manager.getActiveSessionCount();

        Socket socket = mock(Socket.class);
        OutputStream out = new ByteArrayOutputStream();
        when(socket.getOutputStream()).thenReturn(out);
        WebSocketSession session = new WebSocketSession(socket, "테스트3");

        manager.add("테스트3", session);

        assertEquals(initialCount + 1, manager.getActiveSessionCount());

        manager.remove("테스트3");
    }

    @Test
    @DisplayName("존재하지 않는 세션을 확인할 수 있다")
    void hasSessionReturnsFalseForNonExistent() {
        assertFalse(manager.hasSession("존재하지않는세션"));
    }

    @Test
    @DisplayName("특정 세션에게 메시지를 보낼 수 있다")
    void sendToSpecificSession() throws IOException {
        Socket socket = mock(Socket.class);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        when(socket.getOutputStream()).thenReturn(out);
        when(socket.isClosed()).thenReturn(false);
        WebSocketSession session = new WebSocketSession(socket, "테스트4");
        manager.add("테스트4", session);

        manager.sendTo("테스트4", "테스트 메시지");

        assertTrue(out.size() > 0);

        manager.remove("테스트4");
    }
}