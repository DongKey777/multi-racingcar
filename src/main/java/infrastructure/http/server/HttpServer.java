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
    private static final int DEFAULT_PORT = 8080;
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
        int port = resolvePort();
        ServerSocket server = new ServerSocket(port);
        System.out.printf("서버 시작! http://localhost:%d 접속 가능%n", port);

        while (true) {
            Socket client = server.accept();
            System.out.println("연결 완료");

            ClientHandler handler = new ClientHandler(client, router, gameController);
            if (!threadManager.execute(handler::handle)) {
                System.err.println("연결 한도 초과: 요청 거부");
                client.close();
            }
        }
    }

    private int resolvePort() {
        String configuredPort = System.getenv("PORT");
        if (configuredPort == null || configuredPort.isBlank()) {
            return DEFAULT_PORT;
        }
        return Integer.parseInt(configuredPort);
    }
}
