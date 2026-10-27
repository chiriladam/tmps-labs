package hotel.domain.models;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * A hotel reservation. It has 4 required fields and several optional ones,
 * so it is created step by step through its Builder (Builder pattern).
 */
public class Reservation {
    public static final int BREAKFAST_PER_GUEST_PER_NIGHT = 15;
    public static final int PARKING_PER_NIGHT = 10;
    public static final int AIRPORT_TRANSFER = 30;

    // required
    private final String guestName;
    private final Room room;
    private final LocalDate checkIn;
    private final LocalDate checkOut;
    // optional
    private final int guests;
    private final boolean breakfast;
    private final boolean parking;
    private final boolean airportTransfer;
    private final String specialRequest;

    // private: the only way to create a Reservation is through the Builder
    private Reservation(Builder builder) {
        this.guestName = builder.guestName;
        this.room = builder.room;
        this.checkIn = builder.checkIn;
        this.checkOut = builder.checkOut;
        this.guests = builder.guests;
        this.breakfast = builder.breakfast;
        this.parking = builder.parking;
        this.airportTransfer = builder.airportTransfer;
        this.specialRequest = builder.specialRequest;
    }

    public String getGuestName() { return guestName; }
    public Room getRoom() { return room; }
    public LocalDate getCheckIn() { return checkIn; }
    public LocalDate getCheckOut() { return checkOut; }
    public int getGuests() { return guests; }

    public long getNights() {
        return ChronoUnit.DAYS.between(checkIn, checkOut);
    }

    public long getTotalPrice() {
        long nights = getNights();
        long total = nights * room.getPricePerNight();
        if (breakfast) total += nights * guests * BREAKFAST_PER_GUEST_PER_NIGHT;
        if (parking) total += nights * PARKING_PER_NIGHT;
        if (airportTransfer) total += AIRPORT_TRANSFER;
        return total;
    }

    @Override
    public String toString() {
        StringBuilder extras = new StringBuilder();
        if (breakfast) extras.append("breakfast ");
        if (parking) extras.append("parking ");
        if (airportTransfer) extras.append("airport-transfer ");
        return String.format("%s | room %d (%s) | %s -> %s (%d nights) | %d guest(s) | extras: %s| request: %s | total: %d EUR",
                guestName, room.getNumber(), room.getType(), checkIn, checkOut, getNights(), guests,
                extras.length() == 0 ? "none " : extras, specialRequest == null ? "-" : specialRequest,
                getTotalPrice());
    }

    /**
     * Builder: required fields go in the constructor, optional ones are set
     * through chained methods, and build() validates everything at the end.
     */
    public static class Builder {
        private final String guestName;
        private final Room room;
        private final LocalDate checkIn;
        private final LocalDate checkOut;
        private int guests = 1;
        private boolean breakfast;
        private boolean parking;
        private boolean airportTransfer;
        private String specialRequest;

        public Builder(String guestName, Room room, LocalDate checkIn, LocalDate checkOut) {
            this.guestName = guestName;
            this.room = room;
            this.checkIn = checkIn;
            this.checkOut = checkOut;
        }

        public Builder guests(int guests) {
            this.guests = guests;
            return this;
        }

        public Builder withBreakfast() {
            this.breakfast = true;
            return this;
        }

        public Builder withParking() {
            this.parking = true;
            return this;
        }

        public Builder withAirportTransfer() {
            this.airportTransfer = true;
            return this;
        }

        public Builder specialRequest(String request) {
            this.specialRequest = request;
            return this;
        }

        public Reservation build() {
            if (!checkOut.isAfter(checkIn)) {
                throw new IllegalStateException("Check-out must be after check-in");
            }
            if (guests < 1 || guests > room.getCapacity()) {
                throw new IllegalStateException("Room " + room.getNumber() + " (" + room.getType()
                        + ") fits at most " + room.getCapacity() + " guest(s), requested " + guests);
            }
            return new Reservation(this);
        }
    }
}
