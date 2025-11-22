package infrastructure.http.server;

import controller.GameController;
import infrastructure.http.handler.ClientHandler;
import infrastructure.http.router.Router;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import service.GameRoomService;
import service.MatchingService;
import service.PlayerSessionService;

public class HttpServer {
    private static final int PORT = 8080;
    private final Router router;
    private final GameController gameController;
    private final ClientThreadManager threadManager;

    public HttpServer(
            Router router,
            PlayerSessionService sessionService,
            MatchingService matchingService,
            GameRoomService roomService
    ) {
        this.router = router;
        this.gameController = new GameController(sessionService, matchingService, roomService);
        this.threadManager = new ClientThreadManager();
    }

    public void start() throws IOException {
        ServerSocket server = new ServerSocket(PORT);
        System.out.println("서버 시작! http://localhost:8080 접속 가능");

        while (true) {
            Socket client = server.accept();
            System.out.println("연결 완료");

            ClientHandler handler = new ClientHandler(client, router, gameController);
            threadManager.execute(handler::handle);
        }
    }
}