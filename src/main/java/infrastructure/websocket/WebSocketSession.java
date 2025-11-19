package infrastructure.websocket;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class WebSocketSession {
    private final Socket socket;
    private final OutputStream out;
    private final String nickname;

    public WebSocketSession(Socket socket, String nickname) throws IOException {
        this.socket = socket;
        this.out = socket.getOutputStream();
        this.nickname = nickname;
    }

    public void send(String message) {
        try {
            WebSocketFrame.writeText(out, message);
        } catch (IOException e) {
            System.out.println("메시지 전송 실패 [" + nickname + "]: " + e.getMessage());
        }
    }

    public String getNickname() {
        return nickname;
    }

    public boolean isConnected() {
        return socket != null && !socket.isClosed();
    }

    public void close() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            System.out.println("소켓 종료 실패: " + e.getMessage());
        }
    }
}