package solid.srp.good;

/**
 * Single responsibility: sending the invoice by email (communication).
 */
public class EmailService {
    public void send(Invoice invoice, String email) {
        System.out.println("Invoice sent to " + email);
    }
}
