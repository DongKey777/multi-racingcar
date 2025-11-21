package infrastructure.websocket;

import controller.GameController;
import domain.game.GameMode;
import domain.game.PlayerJoinResult;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class WebSocketHandler {
    private final Socket socket;
    private final GameController gameController;

    public WebSocketHandler(Socket socket, GameController gameController) {
        this.socket = socket;
        this.gameController = gameController;
    }

    public void handle() {
        String nickname = null;

        try (
                InputStream in = socket.getInputStream();
                OutputStream out = socket.getOutputStream()
        ) {
            System.out.println("WebSocket 시작");

            PlayerJoinContext context = processPlayerJoin(in, out);
            nickname = context.nickname;

            processGameMessages(in, context.session, nickname);

        } catch (Exception e) {
            System.out.println("연결 종료 [" + nickname + "]: " + e.getMessage());
        } finally {
            gameController.handlePlayerLeave(nickname);
        }
    }

    private PlayerJoinContext processPlayerJoin(InputStream in, OutputStream out) throws Exception {
        while (true) {
            String message = WebSocketFrame.readText(in);
            System.out.println("메시지 수신: " + message);

            PlayerInfo playerInfo = parsePlayerInfo(message);
            String attemptNickname = playerInfo.nickname;
            GameMode mode = playerInfo.mode;

            System.out.println("플레이어 입장 시도: " + attemptNickname + " (모드: " + mode + ")");

            PlayerJoinResult result = gameController.attemptJoinGame(attemptNickname, mode, socket);

            if (result.isSuccess()) {
                WebSocketSession session = new WebSocketSession(socket, attemptNickname);
                return new PlayerJoinContext(attemptNickname, session);
            }

            sendJoinFailureMessage(out, attemptNickname);
        }
    }

    private void sendJoinFailureMessage(OutputStream out, String attemptNickname) throws Exception {
        WebSocketFrame.writeText(out, "입장 실패 (중복 닉네임). 다른 닉네임을 입력해주세요.");
        System.out.println("입장 실패: " + attemptNickname);
    }

    private void processGameMessages(InputStream in, WebSocketSession session, String nickname) throws Exception {
        while (true) {
            String msg = WebSocketFrame.readText(in);

            if (isConnectionClosed(msg, nickname)) {
                break;
            }

            if (!sendMessageToClient(session, msg, nickname)) {
                break;
            }
        }
    }

    private boolean isConnectionClosed(String message, String nickname) {
        if (message == null || message.trim().isEmpty()) {
            System.out.println("연결 종료 감지 [" + nickname + "]: 빈 메시지");
            return true;
        }
        return false;
    }

    private boolean sendMessageToClient(WebSocketSession session, String message, String nickname) {
        System.out.println("메시지 받음 [" + nickname + "]: " + message);

        if (session == null || !session.isConnected()) {
            return false;
        }

        boolean success = session.send("서버: " + message);
        if (!success) {
            System.out.println("메시지 전송 실패로 연결 종료 [" + nickname + "]");
        }
        return success;
    }

    private PlayerInfo parsePlayerInfo(String message) {
        try {
            if (message.startsWith("{") && message.contains("nickname")) {
                String nickname = extractJsonValue(message, "nickname");
                String modeStr = extractJsonValue(message, "mode");
                GameMode mode = "SINGLE".equals(modeStr) ? GameMode.SINGLE : GameMode.MULTI;
                return new PlayerInfo(nickname, mode);
            }
        } catch (Exception e) {
            System.out.println("JSON 파싱 실패, 기본값 사용: " + e.getMessage());
        }
        return new PlayerInfo(message, GameMode.MULTI);
    }

    private String extractJsonValue(String json, String key) {
        String searchKey = "\"" + key + "\"";
        int keyIndex = json.indexOf(searchKey);
        if (keyIndex == -1) {
            return null;
        }

        int colonIndex = json.indexOf(":", keyIndex);
        int valueStart = json.indexOf("\"", colonIndex) + 1;
        int valueEnd = json.indexOf("\"", valueStart);

        return json.substring(valueStart, valueEnd);
    }

    private static class PlayerInfo {
        final String nickname;
        final GameMode mode;

        PlayerInfo(String nickname, GameMode mode) {
            this.nickname = nickname;
            this.mode = mode;
        }
    }

    private static class PlayerJoinContext {
        final String nickname;
        final WebSocketSession session;

        PlayerJoinContext(String nickname, WebSocketSession session) {
            this.nickname = nickname;
            this.session = session;
        }
    }
}