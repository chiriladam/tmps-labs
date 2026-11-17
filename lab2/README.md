# Lab 2 — Structural Design Patterns

**Author:** Macari Chiril-Adam, FAF-243<br>
**Checked by:** asist. univ. Istrati Daniel<br>
**Course:** Techniques and Mechanisms of Software Design

A hotel booking system (continued from Lab 1) that uses three structural patterns on one booking path:

- **Facade**: `FrontDesk`, the single entry point for quotes and bookings
- **Adapter**: `IcsCalendarExporter`, which adapts the third-party [biweekly](https://github.com/mangstadt/biweekly) iCalendar library to the hotel's `CalendarExporter` interface
- **Decorator**: pricing rules (`WeekendSurcharge`, `LoyaltyDiscount`, `TouristTax`) that wrap `BasePrice`

The Lab 1 creational patterns (Singleton, Factory Method, Builder) still create the rooms, reservations and confirmation codes.

📄 **The full report is in [REPORT.md](REPORT.md)**: domain, class diagrams, code, tests and conclusions.

---

## Requirements

- JDK 17 or newer
- Nothing else: the Maven Wrapper (`mvnw` / `mvnw.cmd`) downloads Maven and the libraries (JUnit 5, biweekly) on the first run.

## Build, test and run

All commands are run from the `lab2` folder. On Windows use `mvnw.cmd` instead of `./mvnw`.

```bash
./mvnw test                    # run all unit tests (12 tests: Facade, Adapter, Decorator)
./mvnw -q compile exec:java    # run the console demo (hotel.client.Main)
./mvnw package                 # compile, test and build target/hotel-booking-2.0.jar
```

**IntelliJ IDEA:** File → Open… → select `lab2/pom.xml` → *Open as Project*. Run `src/main/java/hotel/client/Main.java` for the demo, or right-click `src/test/java` → *Run 'All Tests'*.

## Project structure

```
lab2/
├── pom.xml, mvnw, mvnw.cmd, .mvn/      Maven build + wrapper
├── REPORT.md                           the laboratory report
├── diagrams/                           Mermaid sources of the diagrams
└── src/
    ├── main/java/hotel/
    │   ├── client/                     Main (the only client) + HotelConfiguration (wiring)
    │   ├── domain/
    │   │   ├── models/                 rooms, reservation (+ Builder), guest, booking result
    │   │   ├── factory/                RoomFactory + subclasses        (Lab 1)
    │   │   ├── builder/                ReservationDirector             (Lab 1)
    │   │   ├── service/                FrontDesk (Facade), BookingSystem, ReservationNumberGenerator (Lab 1)
    │   │   ├── pricing/                PriceCalculator + decorators, PricingPolicy
    │   │   ├── calendar/               CalendarExporter + IcsCalendarExporter (Adapter)
    │   │   └── payment/                PaymentMethod, CashPayment, CardPayment
    │   ├── external/payflow/           simulated card payment gateway
    │   └── utilities/                  Money formatting
    └── test/java/hotel/domain/         unit tests, one class per pattern
```
