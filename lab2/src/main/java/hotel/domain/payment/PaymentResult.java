package hotel.domain.payment;

public record PaymentResult(boolean successful, String transactionId, String message) {
}
