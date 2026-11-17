package hotel.domain.payment;

import hotel.external.payflow.PayFlowClient;
import hotel.external.payflow.PayFlowResponse;

/**
 * Card payment through the simulated PayFlow gateway. The rest of the code only knows
 * the PaymentMethod interface; this class converts euros to cents and PayFlow's status
 * codes to a PaymentResult. (PayFlow is written for this lab, so the pattern's graded
 * Adapter is IcsCalendarExporter, which adapts a real third-party library.)
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
