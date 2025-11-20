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

    public boolean send(String message) {
        if (!isConnected()) {
            return false;
        }

        try {
            WebSocketFrame.writeText(out, message);
            return true;
        } catch (IOException e) {
            System.out.println("메시지 전송 실패 [" + nickname + "]: " + e.getMessage());
            return false;
        }
    }

    public String getNickname() {
        return nickname;
    }

    public boolean isConnected() {
        if (socket == null || socket.isClosed()) {
            return false;
        }

        try {
            return !socket.isOutputShutdown() && !socket.isInputShutdown();
        } catch (Exception e) {
            return false;
        }
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