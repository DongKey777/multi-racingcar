package infrastructure.websocket;

import domain.game.GameRoomManager;
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

            String nickname = WebSocketFrame.readText(in);
            System.out.println("플레이어 입장 시도: " + nickname);

            GameRoomManager manager = GameRoomManager.getInstance();
            boolean added = manager.addPlayer(nickname);

            if (added) {
                String response = "입장 성공! 대기 중... (" +
                        manager.getWaitingCount() + "/4)";
                WebSocketFrame.writeText(out, response);
                System.out.println("입장 성공: " + nickname);
            } else {
                WebSocketFrame.writeText(out, "입장 실패 (중복 닉네임)");
                System.out.println("입장 실패: " + nickname);
                return;
            }

            while (true) {
                String message = WebSocketFrame.readText(in);
                System.out.println("메시지 받음: " + message);

                WebSocketFrame.writeText(out, "서버: " + message);
            }

        } catch (Exception e) {
            System.out.println("연결 종료: " + e.getMessage());
        }
    }
}