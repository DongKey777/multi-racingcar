package infrastructure.http.server;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

class ClientThreadManagerTest {

    @Test
    void rejectsConnectionsAfterThreadAndQueueCapacityAreExhausted() throws Exception {
        ClientThreadManager manager = new ClientThreadManager(1, 1);
        CountDownLatch running = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);

        assertTrue(manager.execute(() -> {
            running.countDown();
            await(release);
        }));
        assertTrue(running.await(1, TimeUnit.SECONDS));
        assertTrue(manager.execute(() -> await(release)));
        assertFalse(manager.execute(() -> { }));

        release.countDown();
    }

    private void await(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
