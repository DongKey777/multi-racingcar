package infrastructure.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class HttpServer {

    public void start() throws IOException {
        ServerSocket server = new ServerSocket(8080);
        System.out.println("서버 시작! http://localhost:8080 접속 가능");

        while (true) {
            Socket client = server.accept();
            System.out.println("연결 완료");
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

        OutputStream out = client.getOutputStream();
        HttpResponse response = createResponse(httpRequest);

        response.send(out);
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

    private HttpResponse createResponse(HttpRequest request) {
        try {
            String filePath = resolveFilePath(request.getPath());
            String content = readFile(filePath);

            if (filePath.endsWith(".css")) {
                return HttpResponse.css(content);
            }

            if (filePath.endsWith(".js")) {
                return HttpResponse.js(content);
            }

            return HttpResponse.ok(content);
        } catch (IOException e) {
            System.err.println("❌ 파일 없음: " + e.getMessage());
            return HttpResponse.notFound();
        }
    }

    private String resolveFilePath(String path) {
        if (path.equals("/")) {
            return "public/index.html";
        }

        if (path.startsWith("/css/")) {
            return "public" + path;
        }

        if (path.startsWith("/js/")) {
            return "public" + path;
        }

        return "public" + path;
    }

    private String readFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);

        if (!Files.exists(path)) {
            throw new IOException("파일 없음: " + filePath);
        }

        return Files.readString(path);
    }
}