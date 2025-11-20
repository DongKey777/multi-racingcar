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

            GameMode mode = null;
            GameRoomManager manager = GameRoomManager.getInstance();
            WebSocketSession session = null;

            while (nickname == null) {
                String message = WebSocketFrame.readText(in);
                System.out.println("메시지 수신: " + message);

                PlayerInfo playerInfo = parsePlayerInfo(message);
                String attemptNickname = playerInfo.nickname;
                mode = playerInfo.mode;

                System.out.println("플레이어 입장 시도: " + attemptNickname + " (모드: " + mode + ")");

                PlayerJoinResult result = manager.addPlayer(attemptNickname, mode);

                if (!result.isSuccess()) {
                    WebSocketFrame.writeText(out, "입장 실패 (중복 닉네임). 다른 닉네임을 입력해주세요.");
                    System.out.println("입장 실패: " + attemptNickname);
                    continue; // 다시 닉네임 입력 대기
                }

                // 입장 성공
                nickname = attemptNickname;
                session = new WebSocketSession(socket, nickname);
                SessionManager.getInstance().add(nickname, session);

                String welcomeMessage = mode == GameMode.SINGLE
                        ? "입장 성공! 싱글 플레이 시작..."
                        : "입장 성공! 대기 중... (" + result.getWaitingCount() + "/4)";
                session.send(welcomeMessage);
                System.out.println("입장 성공: " + nickname);
            }

            // 게임 중 메시지 처리
            while (true) {
                String msg = WebSocketFrame.readText(in);
                System.out.println("메시지 받음 [" + nickname + "]: " + msg);

                if (session != null) {
                    session.send("서버: " + msg);
                }
            }

        } catch (Exception e) {
            System.out.println("연결 종료 [" + nickname + "]: " + e.getMessage());
        } finally {
            if (nickname != null) {
                SessionManager.getInstance().remove(nickname);
                GameRoomManager.getInstance().removePlayer(nickname);
            }
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
}