package controller;

import domain.game.GameMode;
import domain.game.PlayerJoinResult;
import java.net.Socket;
import service.GameService;

public class GameController {
    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    public PlayerJoinResult attemptJoinGame(String nickname, GameMode mode, Socket socket) throws Exception {
        PlayerJoinResult result = gameService.joinGame(nickname, mode, socket);

        if (result.isSuccess()) {
            System.out.println("입장 성공: " + nickname);
        }

        return result;
    }

    public void handlePlayerLeave(String nickname) {
        gameService.leaveGame(nickname);
    }

    public int getWaitingCount() {
        return gameService.getWaitingPlayerCount();
    }
}
