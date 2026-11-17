package hotel.domain.calendar;

import biweekly.Biweekly;
import biweekly.ICalendar;
import biweekly.component.VEvent;
import biweekly.util.DateTimeComponents;
import biweekly.util.ICalDate;
import hotel.domain.models.DoubleRoom;
import hotel.domain.models.Reservation;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Adapter: our CalendarExporter call is translated into the biweekly library's model.
 * Each test reads the produced text back with biweekly's own parser, so it checks what
 * the third-party library actually understood, not just our string.
 */
class IcsCalendarExporterTest {

    private final CalendarExporter exporter = new IcsCalendarExporter();   // used through the Target interface

    private final Reservation stay = new Reservation.Builder("Maria Popescu", new DoubleRoom(201),
            LocalDate.of(2026, 11, 12), LocalDate.of(2026, 11, 15))
            .guests(2)
            .build();

    private VEvent exportAndParseBack() {
        String ics = exporter.export("RES-0001", stay);
        ICalendar calendar = Biweekly.parse(ics).first();
        assertEquals(1, calendar.getEvents().size());
        return calendar.getEvents().get(0);
    }

    @Test
    void localDatesBecomeAllDayEventDates() {
        VEvent event = exportAndParseBack();

        ICalDate start = event.getDateStart().getValue();
        ICalDate end = event.getDateEnd().getValue();

        assertFalse(start.hasTime(), "a hotel stay is an all-day event");
        assertDate(2026, 11, 12, start.getRawComponents());
        assertDate(2026, 11, 15, end.getRawComponents());
    }

    @Test
    void reservationDataIsTranslatedIntoEventProperties() {
        VEvent event = exportAndParseBack();

        assertEquals("RES-0001@hotel.example", event.getUid().getValue());
        assertEquals("Room 201", event.getLocation().getValue());
        assertEquals("Hotel stay - room 201 (DOUBLE)", event.getSummary().getValue());
        assertEquals("Reservation RES-0001 for Maria Popescu, 2 guest(s), 3 night(s)",
                event.getDescription().getValue());
    }

    @Test
    void outputIsAStandardICalendarDocument() {
        String ics = exporter.export("RES-0001", stay);

        assertTrue(ics.startsWith("BEGIN:VCALENDAR"));
        assertTrue(ics.contains("VERSION:2.0"));
        assertTrue(ics.contains("DTSTART;VALUE=DATE:20261112"));
        assertTrue(ics.contains("DTEND;VALUE=DATE:20261115"));
    }

    private static void assertDate(int year, int month, int day, DateTimeComponents actual) {
        assertEquals(year, actual.getYear());
        assertEquals(month, actual.getMonth());
        assertEquals(day, actual.getDate());
    }
}
