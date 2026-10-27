package hotel.domain.payment;

import hotel.external.payflow.PayFlowClient;
import hotel.external.payflow.PayFlowResponse;

/**
 * Lets the hotel take card payments through the external PayFlow SDK while the rest
 * of the code only knows our PaymentMethod interface. It translates euros to cents
 * and PayFlow's status codes to a PaymentResult.
 */
public class CardPayment implements PaymentMethod {

    private final PayFlowClient payFlow;   // the external class being wrapped
    private final String cardToken;

    public CardPayment(PayFlowClient payFlow, String cardToken) {
        this.payFlow = payFlow;
        this.cardToken = cardToken;
    }

    @Override
    public PaymentResult pay(double amountEur, String reference) {
        long amountInCents = Math.round(amountEur * 100);
        PayFlowResponse response = payFlow.createCharge(cardToken, amountInCents, "EUR", reference);
        if (response.status() == 200) {
            return new PaymentResult(true, response.chargeId(), "paid by card");
        }
        return new PaymentResult(false, null, "card payment failed: " + response.message());
    }
}
