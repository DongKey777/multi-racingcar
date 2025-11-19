package infrastructure.websocket;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {
    private static SessionManager instance;
    private final Map<String, WebSocketSession> sessions;

    private SessionManager() {
        this.sessions = new ConcurrentHashMap<>();
    }

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void add(String nickname, WebSocketSession session) {
        sessions.put(nickname, session);
        System.out.println("✅ 세션 등록: " + nickname + " (총 " + sessions.size() + "명)");
    }

    public void remove(String nickname) {
        WebSocketSession session = sessions.remove(nickname);
        if (session != null) {
            session.close();
            System.out.println("❌ 세션 제거: " + nickname + " (남은 " + sessions.size() + "명)");
        }
    }

    public void broadcast(String message) {
        System.out.println("📢 브로드캐스트 → " + sessions.size() + "명: " + message.trim());
        sessions.values().forEach(session -> {
            if (session.isConnected()) {
                session.send(message);
            }
        });
    }

    public void sendTo(String nickname, String message) {
        WebSocketSession session = sessions.get(nickname);
        if (session != null && session.isConnected()) {
            session.send(message);
            System.out.println("📨 개별 전송 → " + nickname + ": " + message.trim());
        }
    }

    public int getActiveSessionCount() {
        return sessions.size();
    }

    public boolean hasSession(String nickname) {
        return sessions.containsKey(nickname);
    }
}