package hotel.external.payflow;

/**
 * Simulates a third-party card payment SDK called "PayFlow".
 * We cannot change this code: it works in cents, expects an ISO currency code
 * and reports the result through HTTP-like status codes (200 = approved).
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
