package infrastructure.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {

    public void start() throws IOException {
        ServerSocket server = new ServerSocket(8080);
        System.out.println("서버 시작! http://localhost:8080 접속 가능");

        while (true) {
            Socket client = server.accept();
            System.out.println("클라이언트 연결됨");

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(client.getInputStream())
            );

            String requestLine = in.readLine();
            System.out.println("요청: " + requestLine);

            HttpRequest request = HttpRequest.from(requestLine);

            client.close();
        }
    }
}