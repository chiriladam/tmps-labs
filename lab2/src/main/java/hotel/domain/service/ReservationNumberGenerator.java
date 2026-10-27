package hotel.domain.service;

/**
 * Singleton - hands out the hotel's confirmation codes (RES-0001, RES-0002, ...).
 * There must be exactly one generator: two separate counters would both start
 * from 1 and give two different guests the same confirmation code.
 */
public final class ReservationNumberGenerator {

    // volatile: every thread sees the fully constructed instance
    private static volatile ReservationNumberGenerator instance;

    private int lastNumber = 0;

    // private constructor: nobody outside can call "new ReservationNumberGenerator()"
    private ReservationNumberGenerator() {
        System.out.println("[ReservationNumberGenerator] Single instance created");
    }

    // thread-safe lazy initialization with double-checked locking
    public static ReservationNumberGenerator getInstance() {
        if (instance == null) {
            synchronized (ReservationNumberGenerator.class) {
                if (instance == null) {
                    instance = new ReservationNumberGenerator();
                }
            }
        }
        return instance;
    }

    // synchronized: two desks booking at the same moment never get the same code
    public synchronized String next() {
        lastNumber++;
        return String.format("RES-%04d", lastNumber);
    }
}
