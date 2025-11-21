package infrastructure.http.server;

import domain.game.GameRoomManager;
import infrastructure.http.handler.ClientHandler;
import infrastructure.http.router.Router;
import infrastructure.websocket.SessionManager;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {
    private static final int PORT = 8080;
    private final Router router;
    private final SessionManager sessionManager;
    private final GameRoomManager gameRoomManager;

    public HttpServer(Router router, SessionManager sessionManager, GameRoomManager gameRoomManager) {
        this.router = router;
        this.sessionManager = sessionManager;
        this.gameRoomManager = gameRoomManager;
    }

    public void start() throws IOException {
        ServerSocket server = new ServerSocket(PORT);
        System.out.println("서버 시작! http://localhost:8080 접속 가능");

        while (true) {
            Socket client = server.accept();
            System.out.println("연결 완료");

            ClientHandler handler = new ClientHandler(client, router, sessionManager, gameRoomManager);
            Thread thread = new Thread(handler::handle);
            thread.start();
        }
    }
}