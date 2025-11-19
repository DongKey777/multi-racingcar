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
        String nickname = null;

        try (
                InputStream in = socket.getInputStream();
                OutputStream out = socket.getOutputStream()
        ) {
            System.out.println("WebSocket 시작");

            nickname = WebSocketFrame.readText(in);
            System.out.println("플레이어 입장 시도: " + nickname);

            GameRoomManager manager = GameRoomManager.getInstance();
            boolean added = manager.addPlayer(nickname);

            if (!added) {
                WebSocketFrame.writeText(out, "입장 실패 (중복 닉네임)");
                System.out.println("입장 실패: " + nickname);
                return;
            }

            WebSocketSession session = new WebSocketSession(socket, nickname);
            SessionManager.getInstance().add(nickname, session);

            String welcomeMessage = "입장 성공! 대기 중... (" +
                    manager.getWaitingCount() + "/4)";
            session.send(welcomeMessage);
            System.out.println("입장 성공: " + nickname);

            while (true) {
                String message = WebSocketFrame.readText(in);
                System.out.println("메시지 받음 [" + nickname + "]: " + message);

                session.send("서버: " + message);
            }

        } catch (Exception e) {
            System.out.println("연결 종료 [" + nickname + "]: " + e.getMessage());
        } finally {
            if (nickname != null) {
                SessionManager.getInstance().remove(nickname);
            }
        }
    }
}