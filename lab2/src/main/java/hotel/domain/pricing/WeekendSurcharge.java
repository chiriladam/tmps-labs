package hotel.domain.pricing;

import hotel.domain.models.Reservation;

import java.time.DayOfWeek;

/**
 * +20% of the room price for every Friday and Saturday night.
 */
public class WeekendSurcharge extends PriceAdjustment {

    private static final double RATE = 0.20;

    public WeekendSurcharge(PriceCalculator wrapped) {
        super(wrapped);
    }

    @Override
    protected double adjustment(Reservation reservation, double priceSoFar) {
        long weekendNights = reservation.getCheckIn().datesUntil(reservation.getCheckOut())
                .filter(night -> night.getDayOfWeek() == DayOfWeek.FRIDAY
                        || night.getDayOfWeek() == DayOfWeek.SATURDAY)
                .count();
        return weekendNights * reservation.getRoom().getPricePerNight() * RATE;
    }

    @Override
    protected String label() {
        return "weekend surcharge";
    }
}
