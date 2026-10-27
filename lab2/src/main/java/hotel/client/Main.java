package hotel.client;

import hotel.domain.models.BookingResult;
import hotel.domain.models.Guest;
import hotel.domain.models.RoomType;
import hotel.domain.payment.CardPayment;
import hotel.domain.payment.CashPayment;
import hotel.domain.service.BookingSystem;
import hotel.domain.service.FrontDesk;
import hotel.domain.service.ReservationNumberGenerator;
import hotel.external.payflow.PayFlowClient;
import hotel.utilities.Money;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * The only client of the system: it talks to the hotel through the FrontDesk.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("=== Opening the hotel ===");
        FrontDesk frontDesk = new FrontDesk(new BookingSystem(ReservationNumberGenerator.getInstance()));
        PayFlowClient payFlow = new PayFlowClient();   // external card payment SDK

        frontDesk.openRoom(RoomType.SINGLE, 101);
        frontDesk.openRoom(RoomType.SINGLE, 102);
        frontDesk.openRoom(RoomType.DOUBLE, 201);
        frontDesk.openRoom(RoomType.DOUBLE, 202);
        frontDesk.openRoom(RoomType.SUITE, 301);
        frontDesk.rooms().forEach(System.out::println);

        Guest maria = new Guest("Maria Popescu", false);
        Guest ion = new Guest("Ion Rusu", true);
        Guest elena = new Guest("Elena Ciobanu", false);
        Guest andrei = new Guest("Andrei Lungu", false);
        Guest victor = new Guest("Victor Moraru", false);
        LocalDate nov10 = LocalDate.of(2026, 11, 10);
        LocalDate nov12 = LocalDate.of(2026, 11, 12);
        LocalDate nov13 = LocalDate.of(2026, 11, 13);   // a Friday
        LocalDate nov15 = LocalDate.of(2026, 11, 15);

        System.out.println();
        System.out.println("=== Price quotes: double room, 2 guests, Nov 12-15 ===");
        System.out.println("Regular guest:  " + frontDesk.quote(maria, RoomType.DOUBLE, nov12, nov15, 2));
        System.out.println("Loyalty member: " + frontDesk.quote(ion, RoomType.DOUBLE, nov12, nov15, 2));

        System.out.println();
        System.out.println("=== Bookings through the front desk ===");
        List<BookingResult> results = new ArrayList<>();
        results.add(frontDesk.bookRoom(maria, RoomType.DOUBLE, nov12, nov15, 2, new CardPayment(payFlow, "tok_visa_4242")));
        results.add(frontDesk.bookWeekendGetaway(ion, nov13, new CashPayment()));
        results.add(frontDesk.bookBusinessTrip(elena, nov10, 2, new CardPayment(payFlow, "tok_visa_0000")));
        results.add(frontDesk.bookBusinessTrip(elena, nov10, 2, new CashPayment()));
        results.add(frontDesk.bookRoom(andrei, RoomType.SINGLE, nov10, nov12, 3, new CashPayment()));
        results.add(frontDesk.bookRoom(victor, RoomType.SUITE, nov13.plusDays(1), nov13.plusDays(3), 1, new CashPayment()));
        results.forEach(System.out::println);

        long confirmed = results.stream().filter(BookingResult::confirmed).count();
        double revenue = results.stream().mapToDouble(BookingResult::amountPaid).sum();
        System.out.println();
        System.out.println("Confirmed: " + confirmed + " of " + results.size() + " requests, revenue: "
                + Money.eur(revenue) + " EUR");
    }
}
