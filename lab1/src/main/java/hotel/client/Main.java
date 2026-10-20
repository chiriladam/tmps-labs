package hotel.client;

import hotel.domain.builder.ReservationDirector;
import hotel.domain.factory.DoubleRoomFactory;
import hotel.domain.factory.RoomFactory;
import hotel.domain.factory.SingleRoomFactory;
import hotel.domain.factory.SuiteFactory;
import hotel.domain.models.Reservation;
import hotel.domain.models.Room;
import hotel.domain.models.RoomType;
import hotel.domain.service.BookingSystem;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        singletonDemo();
        factoryMethodDemo();
        builderDemo();
    }

    // ---------------------------------------------------------------- Singleton
    private static void singletonDemo() throws InterruptedException {
        System.out.println("=== 1. Singleton: BookingSystem ===");

        // 5 reception desks ask for the booking system at the same time
        Set<BookingSystem> seen = ConcurrentHashMap.newKeySet();
        List<Thread> desks = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            desks.add(new Thread(() -> seen.add(BookingSystem.getInstance())));
        }
        for (Thread desk : desks) desk.start();
        for (Thread desk : desks) desk.join();
        System.out.println("Distinct instances seen by 5 threads: " + seen.size());

        BookingSystem a = BookingSystem.getInstance();
        BookingSystem b = BookingSystem.getInstance();
        System.out.println("getInstance() == getInstance(): " + (a == b));
        System.out.println();
    }

    // ----------------------------------------------------------- Factory Method
    private static void factoryMethodDemo() {
        System.out.println("=== 2. Factory Method: opening rooms ===");

        Map<RoomType, RoomFactory> factories = Map.of(
                RoomType.SINGLE, new SingleRoomFactory(),
                RoomType.DOUBLE, new DoubleRoomFactory(),
                RoomType.SUITE, new SuiteFactory());

        // the client only knows RoomFactory, not the concrete room classes
        factories.get(RoomType.SINGLE).openRoom(101);
        factories.get(RoomType.SINGLE).openRoom(102);
        factories.get(RoomType.DOUBLE).openRoom(201);
        factories.get(RoomType.DOUBLE).openRoom(202);
        factories.get(RoomType.SUITE).openRoom(301);

        for (Room room : BookingSystem.getInstance().getRooms()) {
            System.out.println(room);
        }
        System.out.println();
    }

    // ------------------------------------------------------------------ Builder
    private static void builderDemo() {
        System.out.println("=== 3. Builder: creating reservations ===");
        BookingSystem system = BookingSystem.getInstance();
        LocalDate nov10 = LocalDate.of(2026, 11, 10);
        LocalDate nov13 = LocalDate.of(2026, 11, 13);

        // custom reservation, built step by step
        Room double201 = system.findAvailableRoom(RoomType.DOUBLE, nov10, nov13).orElseThrow();
        Reservation custom = new Reservation.Builder("Maria Popescu", double201, nov10, nov13)
                .guests(2)
                .withBreakfast()
                .withParking()
                .specialRequest("Room on a high floor")
                .build();

        // preset packages, built by the Director
        ReservationDirector director = new ReservationDirector();
        Room suite = system.findAvailableRoom(RoomType.SUITE, nov13, nov13.plusDays(2)).orElseThrow();
        Reservation weekend = director.weekendGetaway("Ion Rusu", suite, nov13);
        Room single = system.findAvailableRoom(RoomType.SINGLE, nov10, nov10.plusDays(2)).orElseThrow();
        Reservation business = director.businessTrip("Elena Ciobanu", single, nov10, 2);

        for (Reservation reservation : List.of(custom, weekend, business)) {
            boolean booked = system.book(reservation);
            System.out.println((booked ? "BOOKED   " : "REJECTED ") + reservation);
        }

        // the builder validates the object before it is created
        try {
            new Reservation.Builder("Andrei Lungu", single, nov10, nov13).guests(3).build();
        } catch (IllegalStateException e) {
            System.out.println("Invalid reservation not built: " + e.getMessage());
        }

        // the Singleton keeps one shared calendar, so the suite cannot be double-booked
        Reservation overlap = new Reservation.Builder("Victor Moraru", suite,
                nov13.plusDays(1), nov13.plusDays(3)).build();
        System.out.println((system.book(overlap) ? "BOOKED   " : "REJECTED ") + overlap);

        long revenue = system.getReservations().stream().mapToLong(Reservation::getTotalPrice).sum();
        System.out.println("Total reservations: " + system.getReservations().size()
                + ", total revenue: " + revenue + " EUR");
    }
}
