package hotel.domain.builder;

import hotel.domain.models.Reservation;
import hotel.domain.models.Room;

import java.time.LocalDate;

/**
 * Builder - the Director.
 * Knows the construction steps for common reservation packages, so the client
 * does not have to repeat them. Same builder, different representations.
 */
public class ReservationDirector {

    public Reservation weekendGetaway(String guestName, Room room, LocalDate friday) {
        return new Reservation.Builder(guestName, room, friday, friday.plusDays(2))
                .guests(2)
                .withBreakfast()
                .specialRequest("Late check-out")
                .build();
    }

    public Reservation businessTrip(String guestName, Room room, LocalDate checkIn, int nights) {
        return new Reservation.Builder(guestName, room, checkIn, checkIn.plusDays(nights))
                .withBreakfast()
                .withParking()
                .withAirportTransfer()
                .build();
    }
}
