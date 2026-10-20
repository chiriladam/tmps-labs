package hotel.domain.factory;

import hotel.domain.models.DoubleRoom;
import hotel.domain.models.Room;

public class DoubleRoomFactory extends RoomFactory {
    @Override
    protected Room createRoom(int number) {
        return new DoubleRoom(number);
    }
}
