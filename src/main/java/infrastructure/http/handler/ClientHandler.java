package infrastructure.http.handler;

import controller.GameController;
import infrastructure.http.request.HttpRequest;
import infrastructure.http.response.HttpResponse;
import infrastructure.http.router.Router;
import infrastructure.websocket.WebSocketHandler;
import infrastructure.websocket.WebSocketHandshake;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import service.GameService;

public class ClientHandler {
    private final Socket client;
    private final Router router;
    private final GameService gameService;

    public ClientHandler(Socket client, Router router, GameService gameService) {
        this.client = client;
        this.router = router;
        this.gameService = gameService;
    }

    public void handle() {
        try (
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(client.getInputStream())
                );
                OutputStream out = client.getOutputStream()
        ) {
            String requestLine = in.readLine();
            if (requestLine == null) {
                return;
            }
            System.out.println("📥 " + requestLine);
            Map<String, String> headers = parseHeaders(in);

            if (isWebSocketRequest(headers)) {
                handleWebSocket(headers, out);
                return;
            }

            HttpRequest request = HttpRequest.from(requestLine);
            HttpResponse response = router.route(request);

            response.send(out);

        } catch (IOException e) {
            System.err.println("클라이언트 처리 오류: " + e.getMessage());
        } finally {
            close(client);
        }
    }

    private Map<String, String> parseHeaders(BufferedReader in) throws IOException {
        Map<String, String> headers = new HashMap<>();

        while (true) {
            String line = in.readLine();

            if (line == null || line.isEmpty()) {
                break;
            }

            addHeaderEntry(headers, line);
        }
        return headers;
    }

    private void addHeaderEntry(Map<String, String> headers, String line) {
        int delimiter = line.indexOf(':');
        if (delimiter <= 0) {
            return;
        }

        String key = line.substring(0, delimiter).trim();
        String value = line.substring(delimiter + 1).trim();
        headers.put(key, value);
    }

    private boolean isWebSocketRequest(Map<String, String> headers) {
        String upgrade = headers.get("Upgrade");
        String key = headers.get("Sec-WebSocket-Key");

        return upgrade != null
                && upgrade.equalsIgnoreCase("websocket")
                && key != null;
    }

    private void handleWebSocket(Map<String, String> headers, OutputStream out)
            throws IOException {
        System.out.println("WebSocket Handshake");

        String clientKey = headers.get("Sec-WebSocket-Key");
        String response = WebSocketHandshake.createResponse(clientKey);

        out.write(response.getBytes());
        out.flush();

        System.out.println("Handshake 완료");

        GameController controller = new GameController(gameService);
        WebSocketHandler handler = new WebSocketHandler(client, controller);
        handler.handle();
    }

    private void close(Socket socket) {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            System.out.println("소켓 종료 에러: " + e.getMessage());
        }
    }
}