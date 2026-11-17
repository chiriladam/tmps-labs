package hotel.domain.payment;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Payment in cash at the reception; implements our interface directly.
 */
public class CashPayment implements PaymentMethod {

    // shared by all cash payments, so receipt numbers never repeat (thread-safe)
    private static final AtomicInteger NEXT_RECEIPT = new AtomicInteger(1);

    @Override
    public PaymentResult pay(double amountEur, String reference) {
        return new PaymentResult(true, "cash receipt #" + NEXT_RECEIPT.getAndIncrement(), "paid in cash");
    }
}
