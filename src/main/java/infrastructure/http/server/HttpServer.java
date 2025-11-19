package infrastructure.http.server;

import infrastructure.http.handler.ClientHandler;
import infrastructure.http.router.Router;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {
    private static final int PORT = 8080;
    private final Router router;

    public HttpServer() {
        this.router = new Router();
    }

    public void start() throws IOException {
        ServerSocket server = new ServerSocket(8080);
        System.out.println("서버 시작! http://localhost:8080 접속 가능");

        while (true) {
            Socket client = server.accept();
            System.out.println("연결 완료");

            ClientHandler handler = new ClientHandler(client, router);
            Thread thread = new Thread(handler::handle);
            thread.start();
        }
    }
}