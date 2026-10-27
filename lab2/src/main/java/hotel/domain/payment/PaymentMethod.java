package hotel.domain.payment;

/**
 * The hotel's own payment interface: amounts in euros, a simple success/failure result.
 */
public interface PaymentMethod {

    PaymentResult pay(double amountEur, String reference);
}
