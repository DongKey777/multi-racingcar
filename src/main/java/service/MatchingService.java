package service;

import domain.game.MatchResult;
import domain.game.Player;
import domain.game.Players;
import domain.game.WaitingQueue;
import infrastructure.websocket.session.SessionManager;

public class MatchingService {
    private final WaitingQueue waitingQueue;
    private final SessionManager sessionManager;

    public MatchingService(WaitingQueue waitingQueue, SessionManager sessionManager) {
        this.waitingQueue = waitingQueue;
        this.sessionManager = sessionManager;
    }

    public MatchResult joinQueue(String nickname) {
        MatchResult result = waitingQueue.addPlayer(nickname);

        if (!result.isMatched()) {
            notifyAllWaitingPlayers(result.getWaitingCount());
        }

        return result;
    }

    public void leaveQueue(String nickname) {
        waitingQueue.removePlayer(nickname);
    }

    private void notifyAllWaitingPlayers(int totalCount) {
        Players waitingPlayers = waitingQueue.getWaitingPlayers();
        String message = createWaitingMessage(totalCount);

        for (Player player : waitingPlayers.getPlayers()) {
            sessionManager.sendTo(player.getNickname(), message);
        }
    }

    private String createWaitingMessage(int count) {
        return "대기 중... (" + count + "/" + Players.MAX_PLAYERS + ")";
    }
}
