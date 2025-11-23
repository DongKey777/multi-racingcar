package service;

import domain.event.GameEventPublisher;
import domain.game.GameRoom;
import domain.game.GameRoomRepository;
import domain.game.Player;
import domain.game.Players;
import domain.game.SingleGameRoom;
import domain.vo.RoomId;
import infrastructure.scheduler.RoomCleanupScheduler;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
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
        startSingleGameLoop(room);
        scheduleRoomCleanup(roomId, true);
    }

    public void createAndStartMultiRoom(Players players) {
        RoomId roomId = generateRoomId();
        String[] nicknames = extractNicknames(players);
        GameRoom room = new GameRoom(nicknames, eventPublisher);

        repository.saveMultiRoom(roomId, room);
        printMultiRoomInfo(roomId, nicknames);

        room.start();
        startMultiGameLoop(room);
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

    private void startSingleGameLoop(SingleGameRoom room) {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(
                () -> playRoundAndStop(room, executor),
                1, 1, TimeUnit.SECONDS
        );
    }

    private void playRoundAndStop(SingleGameRoom room, ScheduledExecutorService executor) {
        boolean continueGame = room.playNextRound();
        if (!continueGame) {
            executor.shutdown();
        }
    }

    private void startMultiGameLoop(GameRoom room) {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(
                () -> playRoundAndStop(room, executor),
                1, 1, TimeUnit.SECONDS
        );
    }

    private void playRoundAndStop(GameRoom room, ScheduledExecutorService executor) {
        boolean continueGame = room.playNextRound();
        if (!continueGame) {
            executor.shutdown();
        }
    }

    private void scheduleRoomCleanup(RoomId roomId, boolean isSingleRoom) {
        scheduler.scheduleCleanup(() -> removeRoom(roomId, isSingleRoom));
    }

    private void removeRoom(RoomId roomId, boolean isSingleRoom) {
        if (isSingleRoom) {
            removeSingleRoom(roomId);
            return;
        }
        removeMultiRoom(roomId);
    }

    private void removeSingleRoom(RoomId roomId) {
        repository.removeSingleRoom(roomId);
        System.out.println("싱글 게임룸 #" + roomId + " 정리 완료");
    }

    private void removeMultiRoom(RoomId roomId) {
        repository.removeMultiRoom(roomId);
        System.out.println("멀티 게임룸 #" + roomId + " 정리 완료");
    }
}
