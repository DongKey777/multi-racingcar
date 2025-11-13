package infrastructure.http;

import java.io.IOException;
import java.io.OutputStream;

public class HttpResponse {
    private final HttpStatus status;
    private final String body;

    public HttpResponse(HttpStatus status, String body) {
        this.status = status;
        this.body = body;
    }

    public static HttpResponse ok(String body) {
        return new HttpResponse(HttpStatus.OK, body);
    }

    public static HttpResponse notFound() {
        return new HttpResponse(HttpStatus.NOT_FOUND, "<h1>404 Not Found</h1>");
    }

    public void send(OutputStream out) throws IOException {
        String response = status.toStatusLine() + "\r\n"
                + "Content-Type: text/html; charset=UTF-8\r\n"
                + "\r\n"
                + body;

        out.write(response.getBytes());
        out.flush();

    }
}
