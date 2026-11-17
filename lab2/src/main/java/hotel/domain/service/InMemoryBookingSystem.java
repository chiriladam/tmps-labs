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
 * Keeps rooms and reservations in memory (the BookingSystem class from Lab 1).
 * It is deliberately NOT a Singleton: like a database, it is created once by the
 * client and passed (injected) to the classes that need it.
 */
public class InMemoryBookingSystem implements BookingSystem {

    private final ReservationNumberGenerator numberGenerator;
    private final Map<Integer, Room> rooms = new LinkedHashMap<>();
    private final Map<String, Reservation> reservations = new LinkedHashMap<>();

    // the generator is injected, so the dependency is visible in the constructor
    public InMemoryBookingSystem(ReservationNumberGenerator numberGenerator) {
        this.numberGenerator = numberGenerator;
    }

    @Override
    public synchronized void addRoom(Room room) {
        rooms.put(room.getNumber(), room);
    }

    @Override
    public synchronized List<Room> getRooms() {
        return List.copyOf(rooms.values());
    }

    @Override
    public synchronized Optional<Room> findAvailableRoom(RoomType type, LocalDate checkIn, LocalDate checkOut) {
        return rooms.values().stream()
                .filter(room -> room.getType() == type)
                .filter(room -> isAvailable(room, checkIn, checkOut))
                .findFirst();
    }

    @Override
    public synchronized Optional<String> book(Reservation reservation) {
        if (!isAvailable(reservation.getRoom(), reservation.getCheckIn(), reservation.getCheckOut())) {
            return Optional.empty();
        }
        String code = numberGenerator.next();
        reservations.put(code, reservation);
        return Optional.of(code);
    }

    @Override
    public synchronized boolean cancel(String code) {
        return reservations.remove(code) != null;
    }

    @Override
    public synchronized List<Reservation> getReservations() {
        return new ArrayList<>(reservations.values());
    }

    private boolean isAvailable(Room room, LocalDate checkIn, LocalDate checkOut) {
        return reservations.values().stream()
                .filter(r -> r.getRoom().getNumber() == room.getNumber())
                .noneMatch(r -> checkIn.isBefore(r.getCheckOut()) && r.getCheckIn().isBefore(checkOut));
    }
}
