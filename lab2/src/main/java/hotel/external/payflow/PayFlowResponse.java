package hotel.external.payflow;

/**
 * Response format of the external PayFlow SDK (HTTP-like status codes).
 */
public record PayFlowResponse(int status, String chargeId, String message) {
}
