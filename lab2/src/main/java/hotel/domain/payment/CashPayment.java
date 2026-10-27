package hotel.domain.payment;

/**
 * Payment in cash at the reception; implements our interface directly.
 */
public class CashPayment implements PaymentMethod {

    private static int nextReceipt = 1;

    @Override
    public PaymentResult pay(double amountEur, String reference) {
        return new PaymentResult(true, "cash receipt #" + nextReceipt++, "paid in cash");
    }
}
