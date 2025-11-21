package infrastructure.websocket;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {
    private final Map<String, WebSocketSession> sessions;

    public SessionManager() {
        this.sessions = new ConcurrentHashMap<>();
    }

    public void add(String nickname, WebSocketSession session) {
        if (sessions.containsKey(nickname)) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다: " + nickname);
        }
        sessions.put(nickname, session);
        System.out.println("세션 등록: " + nickname + " (총 " + sessions.size() + "명)");
    }

    public void remove(String nickname) {
        WebSocketSession session = sessions.remove(nickname);
        if (session != null) {
            session.close();
            System.out.println("세션 제거: " + nickname + " (남은 " + sessions.size() + "명)");
        }
    }

    public void broadcast(String message) {
        System.out.println("브로드캐스트 -> " + sessions.size() + "명: " + message.trim());
        sessions.values().forEach(session -> {
            if (session.isConnected()) {
                session.send(message);
            }
        });
    }

    public void sendTo(String nickname, String message) {
        WebSocketSession session = sessions.get(nickname);
        if (session == null) {
            return;
        }

        if (!session.isConnected()) {
            removeDeadSession(nickname);
            return;
        }

        boolean success = session.send(message);
        if (success) {
            System.out.println("개별 전송 -> " + nickname + ": " + message.trim());
        } else {
            removeDeadSession(nickname);
        }
    }

    private void removeDeadSession(String nickname) {
        WebSocketSession session = sessions.remove(nickname);
        if (session != null) {
            session.close();
            System.out.println("끊어진 세션 자동 제거: " + nickname);
        }
    }

    public boolean hasActiveSession(String nickname) {
        WebSocketSession session = sessions.get(nickname);
        return session != null && session.isConnected();
    }

    public int getActiveSessionCount() {
        return sessions.size();
    }

    public boolean hasSession(String nickname) {
        return sessions.containsKey(nickname);
    }
}