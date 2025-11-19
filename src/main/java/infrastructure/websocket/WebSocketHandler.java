package infrastructure.websocket;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class WebSocketHandler {
    private final Socket socket;

    public WebSocketHandler(Socket socket) {
        this.socket = socket;
    }

    public void handle() {
        try (
                InputStream in = socket.getInputStream();
                OutputStream out = socket.getOutputStream()
        ) {
            System.out.println("WebSocket 시작");

            while (true) {
                String message = WebSocketFrame.readText(in);
                System.out.println("메시지 받음: " + message);

                String response = "서버: " + message;
                WebSocketFrame.writeText(out, response);
            }

        } catch (Exception e) {
            System.out.println("종료: " + e.getMessage());
        }
    }
}