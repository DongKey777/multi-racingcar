package infrastructure.http.response;

import java.io.IOException;
import java.io.OutputStream;

public class HttpResponse {
    private final HttpStatus status;
    private final String contentType;
    private final String body;

    public HttpResponse(HttpStatus status, String contentType, String body) {
        this.status = status;
        this.contentType = contentType;
        this.body = body;
    }

    public static HttpResponse ok(String body) {
        return new HttpResponse(HttpStatus.OK, "text/html; charset=UTF-8", body);
    }

    public static HttpResponse css(String body) {
        return new HttpResponse(HttpStatus.OK, "text/css; charset=UTF-8", body);
    }

    public static HttpResponse js(String body) {
        return new HttpResponse(HttpStatus.OK, "application/javascript; charset=UTF-8", body);
    }

    public static HttpResponse notFound() {
        return new HttpResponse(HttpStatus.NOT_FOUND, "text/html; charset=UTF-8", "<h1>404 Not Found</h1>");
    }

    public void send(OutputStream out) throws IOException {
        String response = status.toStatusLine() + "\r\n"
                + "Content-Type: " + contentType + "\r\n"
                + "\r\n"
                + body;

        out.write(response.getBytes());
        out.flush();
    }
}
