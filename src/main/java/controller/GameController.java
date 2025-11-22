package controller;

import domain.game.GameMode;
import domain.game.MatchResult;
import domain.game.PlayerJoinResult;
import domain.game.Players;
import java.net.Socket;
import service.GameRoomService;
import service.MatchingService;
import service.PlayerSessionService;

public class GameController {
    private final PlayerSessionService sessionService;
    private final MatchingService matchingService;
    private final GameRoomService roomService;

    public GameController(
            PlayerSessionService sessionService,
            MatchingService matchingService,
            GameRoomService roomService
    ) {
        this.sessionService = sessionService;
        this.matchingService = matchingService;
        this.roomService = roomService;
    }

    public PlayerJoinResult attemptJoinGame(String nickname, GameMode mode, Socket socket) throws Exception {
        sessionService.createSession(nickname, socket);

        try {
            if (mode == GameMode.SINGLE) {
                return joinSingleGame(nickname);
            }
            return joinMultiplayerGame(nickname);
        } catch (Exception e) {
            sessionService.closeSession(nickname);
            throw e;
        }
    }

    public void handlePlayerLeave(String nickname) {
        matchingService.leaveQueue(nickname);
        sessionService.closeSession(nickname);
    }

    private PlayerJoinResult joinSingleGame(String nickname) {
        roomService.createAndStartSingleRoom(nickname);
        System.out.println("입장 성공: " + nickname);
        return PlayerJoinResult.success(1, true);
    }

    private PlayerJoinResult joinMultiplayerGame(String nickname) {
        MatchResult result = matchingService.joinQueue(nickname);

        if (result.isMatched()) {
            roomService.createAndStartMultiRoom(result.getPlayers());
            System.out.println("입장 성공: " + nickname);
            return PlayerJoinResult.success(Players.MAX_PLAYERS, true);
        }

        System.out.println("입장 성공: " + nickname);
        return PlayerJoinResult.success(result.getWaitingCount(), false);
    }
}
