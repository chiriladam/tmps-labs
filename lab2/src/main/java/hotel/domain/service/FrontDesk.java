package hotel.domain.service;

import hotel.domain.builder.ReservationDirector;
import hotel.domain.calendar.CalendarExporter;
import hotel.domain.factory.RoomFactory;
import hotel.domain.models.BookingResult;
import hotel.domain.models.Guest;
import hotel.domain.models.Reservation;
import hotel.domain.models.Room;
import hotel.domain.models.RoomType;
import hotel.domain.payment.PaymentMethod;
import hotel.domain.payment.PaymentResult;
import hotel.domain.pricing.PriceCalculator;
import hotel.domain.pricing.PricingPolicy;
import hotel.utilities.Money;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Facade - the single entry point of the hotel for the client.
 * One call here hides the work of the room factories, the reservation builder and
 * director, the booking system, the pricing rules, the payment method and the
 * calendar export. The facade only coordinates: every subsystem is injected through
 * the constructor and does its own work.
 */
public class FrontDesk {

    private final BookingSystem bookingSystem;
    private final Map<RoomType, RoomFactory> roomFactories;
    private final ReservationDirector packages;
    private final PricingPolicy pricingPolicy;
    private final CalendarExporter calendar;

    public FrontDesk(BookingSystem bookingSystem, Map<RoomType, RoomFactory> roomFactories,
                     ReservationDirector packages, PricingPolicy pricingPolicy, CalendarExporter calendar) {
        this.bookingSystem = bookingSystem;
        this.roomFactories = Map.copyOf(roomFactories);
        this.packages = packages;
        this.pricingPolicy = pricingPolicy;
        this.calendar = calendar;
    }

    public void openRoom(RoomType type, int number) {
        RoomFactory factory = roomFactories.get(type);
        if (factory == null) {
            throw new IllegalArgumentException("No factory registered for room type " + type);
        }
        factory.openRoom(number, bookingSystem);
    }

    public List<Room> rooms() {
        return bookingSystem.getRooms();
    }

    // price of a stay without booking it
    public String quote(Guest guest, RoomType type, LocalDate checkIn, LocalDate checkOut, int guests) {
        Optional<Room> room = bookingSystem.findAvailableRoom(type, checkIn, checkOut);
        if (room.isEmpty()) {
            return "no " + type + " room free from " + checkIn + " to " + checkOut;
        }
        Reservation stay;
        try {
            stay = new Reservation.Builder(guest.name(), room.get(), checkIn, checkOut).guests(guests).build();
        } catch (IllegalStateException e) {
            return "cannot quote: " + e.getMessage();
        }
        PriceCalculator pricing = pricingPolicy.pricingFor(guest);
        return pricing.breakdown(stay) + " = " + Money.eur(pricing.calculate(stay)) + " EUR";
    }

    public BookingResult bookRoom(Guest guest, RoomType type, LocalDate checkIn, LocalDate checkOut,
                                  int guests, PaymentMethod payment) {
        return book(guest, type, checkIn, checkOut, payment,
                room -> new Reservation.Builder(guest.name(), room, checkIn, checkOut).guests(guests).build());
    }

    public BookingResult bookWeekendGetaway(Guest guest, LocalDate friday, PaymentMethod payment) {
        return book(guest, RoomType.SUITE, friday, friday.plusDays(2), payment,
                room -> packages.weekendGetaway(guest.name(), room, friday));
    }

    public BookingResult bookBusinessTrip(Guest guest, LocalDate checkIn, int nights, PaymentMethod payment) {
        return book(guest, RoomType.SINGLE, checkIn, checkIn.plusDays(nights), payment,
                room -> packages.businessTrip(guest.name(), room, checkIn, nights));
    }

    // the steps every booking goes through, hidden from the client
    private BookingResult book(Guest guest, RoomType type, LocalDate checkIn, LocalDate checkOut,
                               PaymentMethod payment, Function<Room, Reservation> createReservation) {
        // 1. find a free room
        Optional<Room> room = bookingSystem.findAvailableRoom(type, checkIn, checkOut);
        if (room.isEmpty()) {
            return BookingResult.rejected(guest.name(), "no " + type + " room free from " + checkIn + " to " + checkOut);
        }
        // 2. build the reservation (Builder / Director)
        Reservation reservation;
        try {
            reservation = createReservation.apply(room.get());
        } catch (IllegalStateException e) {
            return BookingResult.rejected(guest.name(), e.getMessage());
        }
        // 3. calculate the price with the pricing rules (decorators) that apply to this guest
        PriceCalculator pricing = pricingPolicy.pricingFor(guest);
        double price = pricing.calculate(reservation);
        // 4. reserve the room and get a confirmation code
        Optional<String> code = bookingSystem.book(reservation);
        if (code.isEmpty()) {
            return BookingResult.rejected(guest.name(), "room " + room.get().getNumber() + " was just taken");
        }
        // 5. take the payment; if it fails, release the room again
        PaymentResult paid = payment.pay(price, code.get());
        if (!paid.successful()) {
            bookingSystem.cancel(code.get());
            return BookingResult.rejected(guest.name(), paid.message() + " (" + code.get() + " cancelled)");
        }
        // 6. create the calendar invite for the guest (Adapter over the biweekly library)
        String invite = calendar.export(code.get(), reservation);
        String details = String.format("room %d (%s) | %s -> %s | %s EUR %s (%s)%n          price: %s",
                room.get().getNumber(), type, checkIn, checkOut, Money.eur(price),
                paid.message(), paid.transactionId(), pricing.breakdown(reservation));
        return BookingResult.confirmed(code.get(), guest.name(), details, price, invite);
    }
}
