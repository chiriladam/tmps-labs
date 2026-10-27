package hotel.domain.factory;

import hotel.domain.models.Room;
import hotel.domain.models.Suite;

public class SuiteFactory extends RoomFactory {
    @Override
    protected Room createRoom(int number) {
        return new Suite(number);
    }
}
