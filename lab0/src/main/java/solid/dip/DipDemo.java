package solid.dip;

import solid.dip.good.Database;
import solid.dip.good.MongoDatabase;
import solid.dip.good.MySQLDatabase;
import solid.dip.good.OrderService;

/**
 * Dependency Inversion Principle demo.
 */
public class DipDemo {
    public static void main(String[] args) {
        System.out.println("=== DIP: Dependency Inversion Principle ===");

        System.out.println("--- Before (concrete dependency, created with new) ---");
        solid.dip.bad.OrderService badService = new solid.dip.bad.OrderService();
        badService.placeOrder("Order #1");

        System.out.println("--- After (dependency injected through the constructor) ---");
        OrderService service = new OrderService(new MySQLDatabase());
        service.placeOrder("Order #1");

        // switch the database without touching OrderService:
        OrderService service2 = new OrderService(new MongoDatabase());
        service2.placeOrder("Order #2");

        // in tests we can inject a fake database, no real server needed:
        Database fakeDatabase = data -> System.out.println("[TEST] Would have saved: " + data);
        OrderService testService = new OrderService(fakeDatabase);
        testService.placeOrder("Test order");
    }
}
