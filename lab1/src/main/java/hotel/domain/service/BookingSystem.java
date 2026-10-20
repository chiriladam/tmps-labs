package hotel.domain.service;

import hotel.domain.models.Reservation;
import hotel.domain.models.Room;
import hotel.domain.models.RoomType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Singleton - the hotel has exactly one booking system, shared by everyone,
 * so all rooms and reservations live in one place and a room cannot be double-booked.
 */
public final class BookingSystem {

    // volatile: every thread sees the fully constructed instance
    private static volatile BookingSystem instance;

    private final Map<Integer, Room> rooms = new LinkedHashMap<>();
    private final List<Reservation> reservations = new ArrayList<>();

    // private constructor: nobody outside can call "new BookingSystem()"
    private BookingSystem() {
        System.out.println("[BookingSystem] Single instance created");
    }

    // thread-safe lazy initialization with double-checked locking
    public static BookingSystem getInstance() {
        if (instance == null) {
            synchronized (BookingSystem.class) {
                if (instance == null) {
                    instance = new BookingSystem();
                }
            }
        }
        return instance;
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

    public synchronized boolean book(Reservation reservation) {
        if (!isAvailable(reservation.getRoom(), reservation.getCheckIn(), reservation.getCheckOut())) {
            return false;
        }
        reservations.add(reservation);
        return true;
    }

    public synchronized List<Reservation> getReservations() {
        return Collections.unmodifiableList(new ArrayList<>(reservations));
    }

    private boolean isAvailable(Room room, LocalDate checkIn, LocalDate checkOut) {
        return reservations.stream()
                .filter(r -> r.getRoom().getNumber() == room.getNumber())
                .noneMatch(r -> checkIn.isBefore(r.getCheckOut()) && r.getCheckIn().isBefore(checkOut));
    }
}
