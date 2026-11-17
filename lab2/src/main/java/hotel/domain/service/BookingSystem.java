package hotel.domain.service;

import hotel.domain.models.Reservation;
import hotel.domain.models.Room;
import hotel.domain.models.RoomType;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Stores the hotel's rooms and reservations.
 * It is an interface so that the FrontDesk depends on an abstraction: the demo uses
 * InMemoryBookingSystem, the unit tests use a stub that records every call.
 */
public interface BookingSystem {

    void addRoom(Room room);

    List<Room> getRooms();

    Optional<Room> findAvailableRoom(RoomType type, LocalDate checkIn, LocalDate checkOut);

    // returns the confirmation code, or empty if the room is already taken for those dates
    Optional<String> book(Reservation reservation);

    // removes a booking again, e.g. when the payment fails
    boolean cancel(String code);

    List<Reservation> getReservations();
}
