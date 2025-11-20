package domain.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RoomRegistry {
    private final Map<Integer, GameRoom> multiRooms;
    private final Map<Integer, SingleGameRoom> singleRooms;

    public RoomRegistry() {
        this.multiRooms = new ConcurrentHashMap<>();
        this.singleRooms = new ConcurrentHashMap<>();
    }

    public void addMultiRoom(int roomId, GameRoom room) {
        multiRooms.put(roomId, room);
    }

    public void addSingleRoom(int roomId, SingleGameRoom room) {
        singleRooms.put(roomId, room);
    }

    public void removeMultiRoom(int roomId) {
        multiRooms.remove(roomId);
    }

    public void removeSingleRoom(int roomId) {
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
