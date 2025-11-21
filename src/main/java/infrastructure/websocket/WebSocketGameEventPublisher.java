package infrastructure.websocket;

import domain.event.GameEventPublisher;
import java.util.List;

public class WebSocketGameEventPublisher implements GameEventPublisher {
    private final SessionManager sessionManager;

    public WebSocketGameEventPublisher(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public void publish(String nickname, String message) {
        sessionManager.sendTo(nickname, message);
    }

    @Override
    public void publishToAll(List<String> nicknames, String message) {
        for (String nickname : nicknames) {
            sessionManager.sendTo(nickname, message);
        }
    }

    @Override
    public boolean hasActiveSession(String nickname) {
        return sessionManager.hasActiveSession(nickname);
    }

    @Override
    public boolean hasSession(String nickname) {
        return sessionManager.hasSession(nickname);
    }
}
