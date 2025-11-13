package infrastructure.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class HttpServer {

    public void start() throws IOException {
        ServerSocket server = new ServerSocket(8080);
        System.out.println("서버 시작! http://localhost:8080 접속 가능");

        while (true) {
            Socket client = server.accept();

            try {
                handleClient(client);
            } catch (IOException e) {
                System.out.println("오류" + e.getMessage());
            } finally {
                client.close();
            }
        }
    }

    private void handleClient(Socket client) throws IOException {
        BufferedReader in = new BufferedReader(
                new InputStreamReader(client.getInputStream())
        );

        String requestLine = in.readLine();

        HttpRequest httpRequest = HttpRequest.from(requestLine);
        Map<String, String> headers = parseHeaders(in);
    }

    private Map<String, String> parseHeaders(BufferedReader in) throws IOException {
        Map<String, String> headers = new HashMap<>();

        while (true) {
            String line = in.readLine();

            if (line == null || line.isEmpty()) {
                break;
            }

            int delimiter = line.indexOf(':');
            if (delimiter > 0) {
                String key = line.substring(0, delimiter).trim();
                String value = line.substring(delimiter + 1).trim();
                headers.put(key, value);
            }
        }
        return headers;
    }
}