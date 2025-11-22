import domain.event.GameEventPublisher;
import infrastructure.http.router.Router;
import infrastructure.http.server.HttpServer;
import infrastructure.websocket.publisher.WebSocketGameEventPublisher;
import infrastructure.websocket.session.SessionManager;
import java.io.IOException;

public class Application {
    public static void main(String[] args) {
        SessionManager sessionManager = new SessionManager();
        GameEventPublisher eventPublisher = new WebSocketGameEventPublisher(sessionManager);

        GameService gameService = new GameService(sessionManager, eventPublisher);

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