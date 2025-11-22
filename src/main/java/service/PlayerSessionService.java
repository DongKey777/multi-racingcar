package service;

import infrastructure.websocket.session.SessionManager;
import infrastructure.websocket.session.WebSocketSession;
import java.net.Socket;

public class PlayerSessionService {
    private final SessionManager sessionManager;

    public PlayerSessionService(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public void createSession(String nickname, Socket socket) throws Exception {
        validateNickname(nickname);
        WebSocketSession session = new WebSocketSession(socket, nickname);
        sessionManager.add(nickname, session);
    }

    public void closeSession(String nickname) {
        sessionManager.remove(nickname);
    }

    public boolean hasActiveSession(String nickname) {
        return sessionManager.hasActiveSession(nickname);
    }

    private void validateNickname(String nickname) {
        if (sessionManager.exists(nickname)) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다");
        }
    }
}
