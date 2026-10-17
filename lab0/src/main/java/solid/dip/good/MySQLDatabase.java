package solid.dip.good;

public class MySQLDatabase implements Database {
    public void save(String data) {
        System.out.println("Saved to MySQL: " + data);
    }
}
