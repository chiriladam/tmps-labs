package hotel.domain.factory;

import hotel.domain.models.Room;
import hotel.domain.models.SingleRoom;

public class SingleRoomFactory extends RoomFactory {
    @Override
    protected Room createRoom(int number) {
        return new SingleRoom(number);
    }
}
