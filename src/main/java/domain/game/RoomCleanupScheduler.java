package domain.game;

public class RoomCleanupScheduler {
    private static final int CLEANUP_DELAY_SECONDS = 10;

    public void scheduleCleanup(Runnable cleanupTask) {
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(CLEANUP_DELAY_SECONDS * 1000);
                cleanupTask.run();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("정리 작업이 중단되었습니다: " + e.getMessage());
            }
        });
        thread.start();
    }
}
