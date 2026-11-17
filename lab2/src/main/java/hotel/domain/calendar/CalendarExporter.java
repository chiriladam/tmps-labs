package hotel.domain.calendar;

import hotel.domain.models.Reservation;

/**
 * The hotel's own interface for calendar invites (the Adapter's Target):
 * it speaks in our terms (a reservation code and a Reservation with LocalDate fields)
 * and returns the invite as text that can be attached to a confirmation e-mail.
 */
public interface CalendarExporter {

    String export(String reservationCode, Reservation reservation);
}
