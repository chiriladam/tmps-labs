package hotel.domain.calendar;

import biweekly.Biweekly;
import biweekly.ICalendar;
import biweekly.component.VEvent;
import hotel.domain.models.Reservation;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * Adapter: implements our CalendarExporter using the third-party "biweekly" library
 * (net.sf.biweekly), which we cannot change. biweekly has its own object model
 * (ICalendar, VEvent, properties), works with java.util.Date instead of LocalDate and
 * writes text through a chained writer. This class translates between the two worlds.
 */
public class IcsCalendarExporter implements CalendarExporter {

    private static final String HOTEL_DOMAIN = "hotel.example";

    @Override
    public String export(String reservationCode, Reservation reservation) {
        VEvent stay = new VEvent();
        stay.setUid(reservationCode + "@" + HOTEL_DOMAIN);
        stay.setSummary("Hotel stay - room " + reservation.getRoom().getNumber()
                + " (" + reservation.getRoom().getType() + ")");
        stay.setLocation("Room " + reservation.getRoom().getNumber());
        stay.setDescription("Reservation " + reservationCode + " for " + reservation.getGuestName()
                + ", " + reservation.getGuests() + " guest(s), " + reservation.getNights() + " night(s)");
        // LocalDate -> java.util.Date; "false" = all-day event (no time of day)
        stay.setDateStart(toDate(reservation.getCheckIn()), false);
        stay.setDateEnd(toDate(reservation.getCheckOut()), false);

        ICalendar calendar = new ICalendar();
        calendar.addEvent(stay);
        return Biweekly.write(calendar).go();
    }

    private static Date toDate(LocalDate date) {
        return Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}
