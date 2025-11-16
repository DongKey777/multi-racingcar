package infrastructure.http.request;

public class HttpRequest {
    private final String method;
    private final String path;
    private final String version;

    private HttpRequest(String method, String path, String version) {
        this.method = method;
        this.path = path;
        this.version = version;
    }

    public static HttpRequest from(String requestLine) {
        String[] parts = requestLine.split(" ");

        if (parts.length != 3) {
            throw new IllegalArgumentException("잘못된 요청: " + requestLine);
        }

        return new HttpRequest(parts[0], parts[1], parts[2]);
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getVersion() {
        return version;
    }

    public boolean isGetRequest() {
        return "GET".equals(method);
    }
}