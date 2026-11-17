package hotel.external.payflow;

/**
 * A simulated card payment gateway called "PayFlow", standing in for a real provider
 * so the demo runs offline. It works in cents, expects an ISO currency code and reports
 * the result through HTTP-like status codes (200 = approved, 402 = declined).
 */
public class PayFlowClient {

    private int nextChargeId = 1001;

    public PayFlowResponse createCharge(String cardToken, long amountInCents, String currency, String description) {
        if (amountInCents <= 0) {
            return new PayFlowResponse(400, null, "amount must be positive");
        }
        if (cardToken.endsWith("0000")) {
            return new PayFlowResponse(402, null, "card declined");
        }
        return new PayFlowResponse(200, "pf_" + nextChargeId++, "approved");
    }
}
