package domain.event;

import java.util.List;

public interface GameEventPublisher {
    void publish(String nickname, String message);

    void publishToAll(List<String> nicknames, String message);

    boolean hasActiveSession(String nickname);

    boolean hasSession(String nickname);
}
