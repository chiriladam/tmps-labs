package solid.srp.good;

/**
 * Single responsibility: saving the invoice (persistence).
 */
public class InvoiceRepository {
    public void save(Invoice invoice) {
        System.out.println("Invoice saved to invoice.txt (total: " + invoice.calculateTotal() + ")");
    }
}
