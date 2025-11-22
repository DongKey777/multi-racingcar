package service;

import domain.event.GameEventPublisher;
import domain.game.GameRoom;
import domain.game.GameRoomRepository;
import domain.game.Player;
import domain.game.Players;
import domain.game.RoomCleanupScheduler;
import domain.game.SingleGameRoom;
import domain.vo.RoomId;
import java.util.concurrent.atomic.AtomicInteger;

public class GameRoomService {
    private final GameRoomRepository repository;
    private final RoomCleanupScheduler scheduler;
    private final GameEventPublisher eventPublisher;
    private final AtomicInteger roomIdGenerator;

    public GameRoomService(
            GameRoomRepository repository,
            RoomCleanupScheduler scheduler,
            GameEventPublisher eventPublisher
    ) {
        this.repository = repository;
        this.scheduler = scheduler;
        this.eventPublisher = eventPublisher;
        this.roomIdGenerator = new AtomicInteger(1);
    }

    public void createAndStartSingleRoom(String nickname) {
        RoomId roomId = generateRoomId();
        SingleGameRoom room = new SingleGameRoom(nickname, eventPublisher);

        repository.saveSingleRoom(roomId, room);
        printSingleRoomInfo(roomId, nickname);

        room.start();
        scheduleRoomCleanup(roomId, true);
    }

    public void createAndStartMultiRoom(Players players) {
        RoomId roomId = generateRoomId();
        String[] nicknames = extractNicknames(players);
        GameRoom room = new GameRoom(nicknames, eventPublisher);

        repository.saveMultiRoom(roomId, room);
        printMultiRoomInfo(roomId, nicknames);

        room.start();
        scheduleRoomCleanup(roomId, false);
    }

    private RoomId generateRoomId() {
        return new RoomId(roomIdGenerator.getAndIncrement());
    }

    private String[] extractNicknames(Players players) {
        return players.getPlayers().stream()
                .map(Player::getNickname)
                .toArray(String[]::new);
    }

    private void printSingleRoomInfo(RoomId roomId, String nickname) {
        System.out.println("\n싱글 게임룸 #" + roomId + " 생성!");
        System.out.println("참가자: " + nickname);
    }

    private void printMultiRoomInfo(RoomId roomId, String[] nicknames) {
        System.out.println("\n멀티 게임룸 #" + roomId + " 생성!");
        System.out.println("참가자: " + String.join(", ", nicknames));
    }

    private void scheduleRoomCleanup(RoomId roomId, boolean isSingleRoom) {
        scheduler.scheduleCleanup(() -> {
            if (isSingleRoom) {
                repository.removeSingleRoom(roomId);
                System.out.println("싱글 게임룸 #" + roomId + " 정리 완료");
            } else {
                repository.removeMultiRoom(roomId);
                System.out.println("멀티 게임룸 #" + roomId + " 정리 완료");
            }
        });
    }
}
