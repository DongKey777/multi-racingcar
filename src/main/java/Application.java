import domain.event.GameEventPublisher;
import domain.game.GameRoomManager;
import infrastructure.http.router.Router;
import infrastructure.http.server.HttpServer;
import infrastructure.websocket.SessionManager;
import infrastructure.websocket.WebSocketGameEventPublisher;
import java.io.IOException;
import service.GameService;

public class Application {
    public static void main(String[] args) {
        SessionManager sessionManager = new SessionManager();
        GameEventPublisher eventPublisher = new WebSocketGameEventPublisher(sessionManager);
        GameRoomManager gameRoomManager = new GameRoomManager(eventPublisher);

        GameService gameService = new GameService(gameRoomManager, sessionManager, eventPublisher);

        Router router = new Router();
        HttpServer server = new HttpServer(router, gameService);

        try {
            server.start();
        } catch (IOException e) {
            System.err.println("서버 시작 실패");
            e.printStackTrace();
        }
    }
}