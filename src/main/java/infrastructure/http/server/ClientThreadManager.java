package infrastructure.http.server;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientThreadManager {
    private static final int DEFAULT_MAX_THREADS = 32;
    private static final int DEFAULT_QUEUE_CAPACITY = 64;
    private static final AtomicInteger THREAD_SEQUENCE = new AtomicInteger();
    private final ThreadPoolExecutor executor;

    public ClientThreadManager() {
        this(DEFAULT_MAX_THREADS, DEFAULT_QUEUE_CAPACITY);
    }

    ClientThreadManager(int maxThreads, int queueCapacity) {
        this.executor = new ThreadPoolExecutor(
                maxThreads,
                maxThreads,
                30,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(queueCapacity),
                runnable -> {
                    Thread thread = new Thread(
                            runnable,
                            "client-" + THREAD_SEQUENCE.incrementAndGet()
                    );
                    thread.setDaemon(true);
                    return thread;
                },
                new ThreadPoolExecutor.AbortPolicy()
        );
        this.executor.allowCoreThreadTimeOut(true);
    }

    public boolean execute(Runnable task) {
        try {
            executor.execute(task);
            return true;
        } catch (RejectedExecutionException e) {
            return false;
        }
    }
}
