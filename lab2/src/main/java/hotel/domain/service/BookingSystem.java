package hotel.domain.service;

import hotel.domain.models.Reservation;
import hotel.domain.models.Room;
import hotel.domain.models.RoomType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Stores the hotel's rooms and reservations (an in-memory repository).
 * It is deliberately NOT a Singleton: like a database, it is created once by the
 * client and passed (injected) to the classes that need it, so it can be replaced
 * or created fresh in tests.
 */
public class BookingSystem {

    private final ReservationNumberGenerator numberGenerator;
    private final Map<Integer, Room> rooms = new LinkedHashMap<>();
    private final Map<String, Reservation> reservations = new LinkedHashMap<>();

    // the generator is injected, so the dependency is visible in the constructor
    public BookingSystem(ReservationNumberGenerator numberGenerator) {
        this.numberGenerator = numberGenerator;
    }

    public synchronized void addRoom(Room room) {
        rooms.put(room.getNumber(), room);
    }

    public synchronized List<Room> getRooms() {
        return List.copyOf(rooms.values());
    }

    public synchronized Optional<Room> findAvailableRoom(RoomType type, LocalDate checkIn, LocalDate checkOut) {
        return rooms.values().stream()
                .filter(room -> room.getType() == type)
                .filter(room -> isAvailable(room, checkIn, checkOut))
                .findFirst();
    }

    // returns the confirmation code, or empty if the room is already taken for those dates
    public synchronized Optional<String> book(Reservation reservation) {
        if (!isAvailable(reservation.getRoom(), reservation.getCheckIn(), reservation.getCheckOut())) {
            return Optional.empty();
        }
        String code = numberGenerator.next();
        reservations.put(code, reservation);
        return Optional.of(code);
    }

    // removes a booking again, e.g. when the payment fails
    public synchronized boolean cancel(String code) {
        return reservations.remove(code) != null;
    }

    public synchronized List<Reservation> getReservations() {
        return new ArrayList<>(reservations.values());
    }

    private boolean isAvailable(Room room, LocalDate checkIn, LocalDate checkOut) {
        return reservations.values().stream()
                .filter(r -> r.getRoom().getNumber() == room.getNumber())
                .noneMatch(r -> checkIn.isBefore(r.getCheckOut()) && r.getCheckIn().isBefore(checkOut));
    }
}
