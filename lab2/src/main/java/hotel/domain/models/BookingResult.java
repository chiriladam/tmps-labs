package hotel.domain.models;

/**
 * What the front desk answers after a booking attempt.
 * A confirmed booking also carries the calendar invite (iCalendar text) for the guest.
 */
public record BookingResult(boolean confirmed, String reservationCode, String guestName,
                            String details, double amountPaid, String calendarInvite) {

    public static BookingResult confirmed(String code, String guestName, String details, double amountPaid,
                                          String calendarInvite) {
        return new BookingResult(true, code, guestName, details, amountPaid, calendarInvite);
    }

    public static BookingResult rejected(String guestName, String reason) {
        return new BookingResult(false, null, guestName, reason, 0, null);
    }

    @Override
    public String toString() {
        return confirmed
                ? "CONFIRMED " + reservationCode + " | " + guestName + " | " + details
                : "REJECTED | " + guestName + " | " + details;
    }
}
