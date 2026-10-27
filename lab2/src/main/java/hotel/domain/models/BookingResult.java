package hotel.domain.models;

/**
 * What the front desk answers after a booking attempt.
 */
public record BookingResult(boolean confirmed, String reservationCode, String guestName,
                            String details, double amountPaid) {

    public static BookingResult confirmed(String code, String guestName, String details, double amountPaid) {
        return new BookingResult(true, code, guestName, details, amountPaid);
    }

    public static BookingResult rejected(String guestName, String reason) {
        return new BookingResult(false, null, guestName, reason, 0);
    }

    @Override
    public String toString() {
        return confirmed
                ? "CONFIRMED " + reservationCode + " | " + guestName + " | " + details
                : "REJECTED | " + guestName + " | " + details;
    }
}
