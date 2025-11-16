package infrastructure.http.router;

import infrastructure.http.request.HttpRequest;
import infrastructure.http.response.HttpResponse;

public class Router {
    private final StaticFileHandler staticFileHandler;

    public Router() {
        this.staticFileHandler = new StaticFileHandler();
    }

    public HttpResponse route(HttpRequest request) {
        String path = request.getPath();

        return staticFileHandler.handle(path);
    }
}