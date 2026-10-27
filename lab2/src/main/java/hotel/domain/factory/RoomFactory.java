package hotel.domain.factory;

import hotel.domain.models.Room;
import hotel.domain.service.BookingSystem;

/**
 * Factory Method - the abstract creator.
 * openRoom() contains the common logic and works only with the Room abstraction;
 * the subclasses decide WHICH concrete Room gets created by overriding createRoom().
 */
public abstract class RoomFactory {

    // the factory method
    protected abstract Room createRoom(int number);

    public Room openRoom(int number, BookingSystem bookingSystem) {
        Room room = createRoom(number);
        bookingSystem.addRoom(room);
        return room;
    }
}
