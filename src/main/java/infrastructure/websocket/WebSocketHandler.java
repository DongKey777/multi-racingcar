package infrastructure.websocket;

import domain.game.GameMode;
import domain.game.GameRoomManager;
import domain.game.PlayerJoinResult;
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

            PlayerJoinContext context = processPlayerJoin(in, out);
            nickname = context.nickname;

            processGameMessages(in, context.session, nickname);

        } catch (Exception e) {
            System.out.println("연결 종료 [" + nickname + "]: " + e.getMessage());
        } finally {
            cleanupPlayer(nickname);
        }
    }

    private PlayerJoinContext processPlayerJoin(InputStream in, OutputStream out) throws Exception {
        GameRoomManager manager = GameRoomManager.getInstance();

        while (true) {
            String message = WebSocketFrame.readText(in);
            System.out.println("메시지 수신: " + message);

            PlayerInfo playerInfo = parsePlayerInfo(message);
            String attemptNickname = playerInfo.nickname;
            GameMode mode = playerInfo.mode;

            System.out.println("플레이어 입장 시도: " + attemptNickname + " (모드: " + mode + ")");

            PlayerJoinResult result = manager.addPlayer(attemptNickname, mode);

            if (result.isSuccess()) {
                return registerPlayer(attemptNickname, mode, result);
            }

            sendJoinFailureMessage(out, attemptNickname);
        }
    }

    private PlayerJoinContext registerPlayer(String nickname, GameMode mode, PlayerJoinResult result) throws Exception {
        WebSocketSession session = new WebSocketSession(socket, nickname);
        SessionManager.getInstance().add(nickname, session);

        String welcomeMessage = createWelcomeMessage(mode, result);
        session.send(welcomeMessage);
        System.out.println("입장 성공: " + nickname);

        return new PlayerJoinContext(nickname, session);
    }

    private String createWelcomeMessage(GameMode mode, PlayerJoinResult result) {
        if (mode == GameMode.SINGLE) {
            return "입장 성공! 싱글 플레이 시작...";
        }
        return "입장 성공! 대기 중... (" + result.getWaitingCount() + "/4)";
    }

    private void sendJoinFailureMessage(OutputStream out, String attemptNickname) throws Exception {
        WebSocketFrame.writeText(out, "입장 실패 (중복 닉네임). 다른 닉네임을 입력해주세요.");
        System.out.println("입장 실패: " + attemptNickname);
    }

    private void processGameMessages(InputStream in, WebSocketSession session, String nickname) throws Exception {
        while (true) {
            String msg = WebSocketFrame.readText(in);
            System.out.println("메시지 받음 [" + nickname + "]: " + msg);

            if (session != null) {
                session.send("서버: " + msg);
            }
        }
    }

    private void cleanupPlayer(String nickname) {
        if (nickname != null) {
            SessionManager.getInstance().remove(nickname);
            GameRoomManager.getInstance().removePlayer(nickname);
        }
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