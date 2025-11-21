package infrastructure.http.server;

public class ClientThreadManager {

    public void execute(Runnable task) {
        Thread thread = new Thread(task);
        thread.start();
    }
}
