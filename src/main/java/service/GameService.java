package service;

import domain.event.GameEventPublisher;
import domain.game.GameMode;
import domain.game.GameRoomManager;
import domain.game.PlayerJoinResult;
import infrastructure.websocket.SessionManager;
import infrastructure.websocket.WebSocketSession;
import java.net.Socket;

public class GameService {
    private final GameRoomManager gameRoomManager;
    private final SessionManager sessionManager;
    private final GameEventPublisher eventPublisher;

    public GameService(
            GameRoomManager gameRoomManager,
            SessionManager sessionManager,
            GameEventPublisher eventPublisher) {
        this.gameRoomManager = gameRoomManager;
        this.sessionManager = sessionManager;
        this.eventPublisher = eventPublisher;
    }

    public PlayerJoinResult joinGame(String nickname, GameMode mode, Socket socket) throws Exception {
        PlayerJoinResult result = gameRoomManager.addPlayer(nickname, mode);

        if (!result.isSuccess()) {
            return result;
        }

        try {
            WebSocketSession session = new WebSocketSession(socket, nickname);
            sessionManager.add(nickname, session);
            return result;
        } catch (Exception e) {
            gameRoomManager.removePlayer(nickname);
            throw e;
        }
    }

    public void leaveGame(String nickname) {
        if (nickname == null) {
            return;
        }

        gameRoomManager.removePlayer(nickname);
        sessionManager.remove(nickname);
    }

    public int getWaitingPlayerCount() {
        return gameRoomManager.getWaitingCount();
    }

    public int getActiveRoomCount() {
        return gameRoomManager.getActiveRoomCount();
    }

    public void sendWelcomeMessage(String nickname, GameMode mode, PlayerJoinResult result) {
        String message = createWelcomeMessage(mode, result);
        sessionManager.sendTo(nickname, message);
    }

    private String createWelcomeMessage(GameMode mode, PlayerJoinResult result) {
        if (mode == GameMode.SINGLE) {
            return "입장 성공! 싱글 플레이 시작...";
        }
        return "입장 성공! 대기 중... (" + result.getWaitingCount() + "/4)";
    }
}
