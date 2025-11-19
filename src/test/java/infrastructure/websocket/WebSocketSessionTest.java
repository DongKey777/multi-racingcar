package infrastructure.websocket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class WebSocketSessionTest {

    @Test
    @DisplayName("세션을 생성할 수 있다")
    void createSession() throws IOException {
        Socket socket = mock(Socket.class);
        OutputStream out = new ByteArrayOutputStream();
        when(socket.getOutputStream()).thenReturn(out);

        WebSocketSession session = new WebSocketSession(socket, "테스트");

        assertEquals("테스트", session.getNickname());
    }

    @Test
    @DisplayName("연결 상태를 확인할 수 있다")
    void isConnected() throws IOException {
        Socket socket = mock(Socket.class);
        OutputStream out = new ByteArrayOutputStream();
        when(socket.getOutputStream()).thenReturn(out);
        when(socket.isClosed()).thenReturn(false);

        WebSocketSession session = new WebSocketSession(socket, "테스트");

        boolean connected = session.isConnected();

        assertTrue(connected);
    }

    @Test
    @DisplayName("닫힌 소켓은 연결되지 않은 것으로 표시된다")
    void isNotConnectedWhenClosed() throws IOException {
        Socket socket = mock(Socket.class);
        OutputStream out = new ByteArrayOutputStream();
        when(socket.getOutputStream()).thenReturn(out);
        when(socket.isClosed()).thenReturn(true);

        WebSocketSession session = new WebSocketSession(socket, "테스트");

        boolean connected = session.isConnected();

        assertFalse(connected);
    }

    @Test
    @DisplayName("세션을 닫을 수 있다")
    void closeSession() throws IOException {
        Socket socket = mock(Socket.class);
        OutputStream out = new ByteArrayOutputStream();
        when(socket.getOutputStream()).thenReturn(out);
        when(socket.isClosed()).thenReturn(false);

        WebSocketSession session = new WebSocketSession(socket, "테스트");

        session.close();

        verify(socket).close();
    }

    @Test
    @DisplayName("닉네임을 조회할 수 있다")
    void getNickname() throws IOException {
        Socket socket = mock(Socket.class);
        OutputStream out = new ByteArrayOutputStream();
        when(socket.getOutputStream()).thenReturn(out);

        WebSocketSession session = new WebSocketSession(socket, "동훈");

        assertEquals("동훈", session.getNickname());
    }
}