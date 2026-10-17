package solid.dip.bad;

/**
 * DIP VIOLATION: the high-level logic (OrderService) creates its own
 * concrete dependency (MySQLDatabase), so it is "glued" to MySQL.
 */
public class OrderService {
    private MySQLDatabase database = new MySQLDatabase();

    public void placeOrder(String order) {
        database.save(order);
    }
}
