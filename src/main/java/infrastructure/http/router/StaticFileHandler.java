package infrastructure.http.router;

import infrastructure.http.response.HttpResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class StaticFileHandler {
    private static final String PUBLIC_DIR = "public";

    public HttpResponse handle(String path) {
        try {
            String filePath = resolveFilePath(path);
            String content = readFile(filePath);

            return createResponse(filePath, content);
        } catch (IOException e) {
            System.err.println("파일 없음: " + e.getMessage());
            return HttpResponse.notFound();
        }
    }

    private String resolveFilePath(String path) {
        if (path.equals("/")) {
            return PUBLIC_DIR + "/index.html";
        }

        if (path.startsWith("/css/") || path.startsWith("/js/")) {
            return PUBLIC_DIR + path;
        }

        return PUBLIC_DIR + path;
    }

    private String readFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);

        if (!Files.exists(path)) {
            throw new IOException("파일 없음: " + filePath);
        }

        return Files.readString(path);
    }

    private HttpResponse createResponse(String filePath, String content) {
        if (filePath.endsWith(".css")) {
            return HttpResponse.css(content);
        }

        if (filePath.endsWith(".js")) {
            return HttpResponse.js(content);
        }

        return HttpResponse.ok(content);
    }
}
