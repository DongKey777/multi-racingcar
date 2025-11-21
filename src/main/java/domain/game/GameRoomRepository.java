package domain.game;

import domain.vo.RoomId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GameRoomRepository {
    private final Map<RoomId, GameRoom> multiRooms;
    private final Map<RoomId, SingleGameRoom> singleRooms;

    public GameRoomRepository() {
        this.multiRooms = new ConcurrentHashMap<>();
        this.singleRooms = new ConcurrentHashMap<>();
    }

    public void saveMultiRoom(RoomId roomId, GameRoom room) {
        multiRooms.put(roomId, room);
    }

    public void saveSingleRoom(RoomId roomId, SingleGameRoom room) {
        singleRooms.put(roomId, room);
    }

    public void removeMultiRoom(RoomId roomId) {
        multiRooms.remove(roomId);
    }

    public void removeSingleRoom(RoomId roomId) {
        singleRooms.remove(roomId);
    }

    public int getTotalRoomCount() {
        return multiRooms.size() + singleRooms.size();
    }

    public List<GameRoom> getMultiRooms() {
        return new ArrayList<>(multiRooms.values());
    }

    public List<SingleGameRoom> getSingleRooms() {
        return new ArrayList<>(singleRooms.values());
    }
}
