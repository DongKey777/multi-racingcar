import infrastructure.http.server.HttpServer;
import java.io.IOException;

public class Application {
    public static void main(String[] args) {
        HttpServer server = new HttpServer();

        try {
            server.start();
        } catch (IOException e) {
            System.err.println("서버 시작 실패");
            e.printStackTrace();
        }
    }
}