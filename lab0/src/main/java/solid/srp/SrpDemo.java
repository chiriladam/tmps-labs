package solid.srp;

/**
 * Single Responsibility Principle demo.
 */
public class SrpDemo {
    public static void main(String[] args) {
        System.out.println("=== SRP: Single Responsibility Principle ===");

        System.out.println("--- Before (one class does everything) ---");
        solid.srp.bad.Invoice badInvoice = new solid.srp.bad.Invoice(100);
        System.out.println("Total: " + badInvoice.calculateTotal());
        badInvoice.saveToFile();
        badInvoice.sendEmail("client@example.com");

        System.out.println("--- After (separate responsibilities) ---");
        solid.srp.good.Invoice invoice = new solid.srp.good.Invoice(100);
        solid.srp.good.InvoiceRepository repository = new solid.srp.good.InvoiceRepository();
        solid.srp.good.EmailService emailService = new solid.srp.good.EmailService();

        System.out.println("Total: " + invoice.calculateTotal());
        repository.save(invoice);
        emailService.send(invoice, "client@example.com");
    }
}
