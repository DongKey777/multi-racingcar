import domain.event.GameEventPublisher;
import domain.game.GameRoomRepository;
import domain.game.WaitingQueue;
import infrastructure.http.router.Router;
import infrastructure.http.server.HttpServer;
import infrastructure.websocket.publisher.WebSocketGameEventPublisher;
import infrastructure.websocket.session.SessionManager;
import java.io.IOException;
import service.GameRoomService;
import service.MatchingService;
import service.PlayerSessionService;

public class Application {
    public static void main(String[] args) {
        SessionManager sessionManager = new SessionManager();
        GameEventPublisher eventPublisher = new WebSocketGameEventPublisher(sessionManager);

        PlayerSessionService sessionService = new PlayerSessionService(sessionManager);
        MatchingService matchingService = new MatchingService(new WaitingQueue(), sessionManager);
        GameRoomService roomService = new GameRoomService(
                new GameRoomRepository(),
                new RoomCleanupScheduler(),
                eventPublisher
        );

        Router router = new Router();
        HttpServer server = new HttpServer(router, sessionService, matchingService, roomService);

        try {
            server.start();
        } catch (IOException e) {
            System.err.println("서버 시작 실패");
            e.printStackTrace();
        }
    }
}