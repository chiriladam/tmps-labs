package hotel.domain.service;

import hotel.domain.builder.ReservationDirector;
import hotel.domain.calendar.CalendarExporter;
import hotel.domain.models.BookingResult;
import hotel.domain.models.DoubleRoom;
import hotel.domain.models.Guest;
import hotel.domain.models.Reservation;
import hotel.domain.models.Room;
import hotel.domain.models.RoomType;
import hotel.domain.models.SingleRoom;
import hotel.domain.payment.PaymentMethod;
import hotel.domain.payment.PaymentResult;
import hotel.domain.pricing.PriceCalculator;
import hotel.domain.pricing.PricingPolicy;
import hotel.utilities.Money;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Facade: FrontDesk must call its subsystems in the right order and stop at the right
 * step. Every subsystem is replaced by a stub that writes what happened into "calls".
 */
class FrontDeskTest {

    private static final LocalDate NOV_12 = LocalDate.of(2026, 11, 12);
    private static final LocalDate NOV_15 = LocalDate.of(2026, 11, 15);
    private static final Guest MARIA = new Guest("Maria Popescu", false);

    private final List<String> calls = new ArrayList<>();

    /** BookingSystem stub: one free room (or none), always hands out the code RES-TEST. */
    private class StubBookingSystem implements BookingSystem {
        private final Room freeRoom;

        StubBookingSystem(Room freeRoom) {
            this.freeRoom = freeRoom;
        }

        @Override
        public Optional<Room> findAvailableRoom(RoomType type, LocalDate checkIn, LocalDate checkOut) {
            calls.add("find " + type + " room");
            return Optional.ofNullable(freeRoom);
        }

        @Override
        public Optional<String> book(Reservation reservation) {
            calls.add("book room " + reservation.getRoom().getNumber());
            return Optional.of("RES-TEST");
        }

        @Override
        public boolean cancel(String code) {
            calls.add("cancel " + code);
            return true;
        }

        @Override public void addRoom(Room room) { }
        @Override public List<Room> getRooms() { return List.of(); }
        @Override public List<Reservation> getReservations() { return List.of(); }
    }

    private final PricingPolicy fixedPrice = guest -> new PriceCalculator() {
        @Override
        public double calculate(Reservation reservation) {
            calls.add("calculate price");
            return 100.0;
        }

        @Override
        public String breakdown(Reservation reservation) {
            return "stub price 100.00";
        }
    };

    private final CalendarExporter calendar = (code, reservation) -> {
        calls.add("export " + code);
        return "INVITE " + code;
    };

    private PaymentMethod payment(boolean succeeds) {
        return (amount, reference) -> {
            calls.add("pay " + Money.eur(amount) + " for " + reference);
            return succeeds
                    ? new PaymentResult(true, "tx-1", "paid")
                    : new PaymentResult(false, null, "declined");
        };
    }

    private FrontDesk frontDeskWith(Room freeRoom) {
        return new FrontDesk(new StubBookingSystem(freeRoom), Map.of(), new ReservationDirector(),
                fixedPrice, calendar);
    }

    @Test
    void bookingCallsTheSubsystemsInTheRightOrder() {
        BookingResult result = frontDeskWith(new DoubleRoom(201))
                .bookRoom(MARIA, RoomType.DOUBLE, NOV_12, NOV_15, 2, payment(true));

        assertEquals(List.of(
                "find DOUBLE room",
                "calculate price",
                "book room 201",
                "pay 100.00 for RES-TEST",
                "export RES-TEST"), calls);
        assertTrue(result.confirmed());
        assertEquals("INVITE RES-TEST", result.calendarInvite());
    }

    @Test
    void failedPaymentCancelsTheReservationAndSendsNoInvite() {
        BookingResult result = frontDeskWith(new DoubleRoom(201))
                .bookRoom(MARIA, RoomType.DOUBLE, NOV_12, NOV_15, 2, payment(false));

        assertEquals(List.of(
                "find DOUBLE room",
                "calculate price",
                "book room 201",
                "pay 100.00 for RES-TEST",
                "cancel RES-TEST"), calls);
        assertFalse(result.confirmed());
    }

    @Test
    void noFreeRoomStopsBeforePricingAndPayment() {
        BookingResult result = frontDeskWith(null)
                .bookRoom(MARIA, RoomType.DOUBLE, NOV_12, NOV_15, 2, payment(true));

        assertEquals(List.of("find DOUBLE room"), calls);
        assertFalse(result.confirmed());
    }

    @Test
    void quoteForTooManyGuestsReturnsAMessageInsteadOfThrowing() {
        String quote = frontDeskWith(new SingleRoom(101))
                .quote(MARIA, RoomType.SINGLE, NOV_12, NOV_15, 3);

        assertTrue(quote.contains("fits at most 1 guest(s), requested 3"), quote);
        assertFalse(calls.contains("calculate price"));
    }
}
