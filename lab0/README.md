<div align="center">

**MINISTRY OF EDUCATION, CULTURE AND RESEARCH OF THE REPUBLIC OF MOLDOVA**<br>
**Technical University of Moldova**<br>
**Faculty of Computers, Informatics and Microelectronics**<br>
**Department of Software Engineering and Automation**

<br>

**Macari Chiril-Adam, FAF-243**

<br>

# Report

*Lab Work No.0*

***Techniques and Mechanisms of Software Design***

SOLID Principles

<br>

**Chișinău – 2026**

</div>

---

**Principles covered:** Single Responsibility, Open/Closed, Dependency Inversion<br>
**Language used:** Java

---

## 1. Introduction

As an application grows, its code becomes harder and harder to maintain. A small change in one place can break features in other parts of the system. Classes grow to hundreds of lines, and adding a new feature requires changes to code that has already been tested. To prevent these problems, object-oriented programming has developed a set of design principles known by the acronym **SOLID**.

The term was popularized by Robert C. Martin ("Uncle Bob") in the early 2000s. Each letter stands for one principle:

| Letter | Principle | Core idea |
|---|---|---|
| **S** | Single Responsibility Principle | A class has a single responsibility |
| **O** | Open/Closed Principle | Code is open for extension, closed for modification |
| **L** | Liskov Substitution Principle | A subclass can replace its parent class without errors |
| **I** | Interface Segregation Principle | Several small interfaces instead of one large one |
| **D** | Dependency Inversion Principle | Modules depend on abstractions, not on concrete implementations |

The goal of these principles is code that is **easy to understand, modify, extend and test**. This report analyzes three of them: **SRP**, **OCP** and **DIP**. They were chosen because they are among the most widely used in practice and their effects are easy to see in code. For each principle, a Java example is presented in two versions, one that violates the principle and one that follows it, followed by an explanation of the differences.

---

## 2. Single Responsibility Principle (SRP)

### 2.1 Description

> *A class should have only one reason to change.*

Each class should handle a single, well-defined task. If a class has several responsibilities, any change related to one of them forces the whole class to be modified. This increases the risk of introducing bugs into the parts that had nothing to do with the change.

### 2.2 Example that violates the principle

```java
public class Invoice {
    private double amount;

    public Invoice(double amount) {
        this.amount = amount;
    }

    public double calculateTotal() {
        return amount * 1.20; // add 20% VAT
    }

    public void saveToFile() {
        // write the invoice to a file
        System.out.println("Invoice saved to invoice.txt (total: " + calculateTotal() + ")");
    }

    public void sendEmail(String email) {
        // send the invoice by email
        System.out.println("Invoice sent to " + email);
    }
}
```

The `Invoice` class has three different responsibilities:

- **calculating** the total (business logic);
- **saving** the invoice to a file (persistence);
- **sending** the invoice by email (communication).

As a result, the class has three reasons to change: a change in the VAT rate, a change in how invoices are saved (for example, moving to a database) or a change in the email service. Each of these changes touches the same class and can affect the other features.

### 2.3 Example that follows the principle

```java
public class Invoice {
    private double amount;

    public Invoice(double amount) {
        this.amount = amount;
    }

    public double calculateTotal() {
        return amount * 1.20; // add 20% VAT
    }
}

public class InvoiceRepository {
    public void save(Invoice invoice) {
        System.out.println("Invoice saved to invoice.txt (total: " + invoice.calculateTotal() + ")");
    }
}

public class EmailService {
    public void send(Invoice invoice, String email) {
        System.out.println("Invoice sent to " + email);
    }
}
```

### 2.4 Explanation

The responsibilities have been split into three classes, each with a single role:

- `Invoice` contains only the invoice data and the total calculation;
- `InvoiceRepository` handles only saving;
- `EmailService` handles only sending emails.

If it is decided that invoices should be saved to a database instead of a file, only `InvoiceRepository` changes. The `Invoice` and `EmailService` classes stay untouched. The resulting classes are smaller, easier to read and can be tested separately. For example, the total calculation can be tested without creating files or sending emails.

---

## 3. Open/Closed Principle (OCP)

### 3.1 Description

> *Software entities should be open for extension, but closed for modification.*

New functionality should be added by writing new code, not by modifying existing code that has already been tested. In Java, this is usually achieved through interfaces (or abstract classes) and polymorphism.

### 3.2 Example that violates the principle

```java
public class DiscountCalculator {
    public double calculate(String customerType, double price) {
        if (customerType.equals("REGULAR")) {
            return price * 0.95;  // 5% discount
        } else if (customerType.equals("VIP")) {
            return price * 0.80;  // 20% discount
        } else if (customerType.equals("STUDENT")) {
            return price * 0.90;  // 10% discount
        }
        return price;
    }
}
```

The class calculates the discount based on the customer type, using a chain of `if / else if` statements. The problem appears when a new customer type has to be added, for example "PENSIONER". The only option is to open the class and add another `else if` branch. The method grows with every new type, and each change can break a case that already worked, so the code has to be retested in full.

### 3.3 Example that follows the principle

```java
public interface Discount {
    double apply(double price);
}

public class RegularDiscount implements Discount {
    public double apply(double price) {
        return price * 0.95;  // 5% discount
    }
}

public class VipDiscount implements Discount {
    public double apply(double price) {
        return price * 0.80;  // 20% discount
    }
}

public class StudentDiscount implements Discount {
    public double apply(double price) {
        return price * 0.90;  // 10% discount
    }
}

public class DiscountCalculator {
    public double calculate(Discount discount, double price) {
        return discount.apply(price);
    }
}
```

### 3.4 Explanation

A `Discount` interface has been introduced, with a single method, `apply()`. Each discount type is now a separate class that implements this interface. `DiscountCalculator` no longer knows about customer types. It receives a `Discount` object and calls `apply()`, and the concrete behavior is selected through **polymorphism**.

To add the pensioner discount, a new class is enough:

```java
public class PensionerDiscount implements Discount {
    public double apply(double price) {
        return price * 0.85;  // 15% discount
    }
}
```

The `DiscountCalculator` class was not modified at all. The system was **extended** without the existing code being **modified**, which is exactly what the principle states.

---

## 4. Dependency Inversion Principle (DIP)

### 4.1 Description

> *High-level modules should not depend on low-level modules. Both should depend on abstractions.*

The main logic of the application (for example, order processing) should not be tied directly to concrete technical details, such as a particular type of database. The connection between them should go through an interface.

### 4.2 Example that violates the principle

```java
public class MySQLDatabase {
    public void save(String data) {
        System.out.println("Saved to MySQL: " + data);
    }
}

public class OrderService {
    private MySQLDatabase database = new MySQLDatabase();

    public void placeOrder(String order) {
        database.save(order);
    }
}
```

`OrderService` is a high-level module, because it contains the order placement logic. However, it creates a `MySQLDatabase` object by itself with `new`, which is a concrete, low-level implementation. This causes two problems:

- switching to another database (PostgreSQL, MongoDB) requires modifying the `OrderService` class;
- `OrderService` cannot be tested without a real MySQL database, because the dependency cannot be replaced.

### 4.3 Example that follows the principle

```java
public interface Database {
    void save(String data);
}

public class MySQLDatabase implements Database {
    public void save(String data) {
        System.out.println("Saved to MySQL: " + data);
    }
}

public class MongoDatabase implements Database {
    public void save(String data) {
        System.out.println("Saved to MongoDB: " + data);
    }
}

public class OrderService {
    private final Database database;

    public OrderService(Database database) {  // the dependency comes from outside
        this.database = database;
    }

    public void placeOrder(String order) {
        database.save(order);
    }
}
```

Usage:

```java
OrderService service = new OrderService(new MySQLDatabase());
service.placeOrder("Order #1");

// switch the database without touching OrderService:
OrderService service2 = new OrderService(new MongoDatabase());
service2.placeOrder("Order #2");

// in tests we can inject a fake database, no real server needed:
Database fakeDatabase = data -> System.out.println("[TEST] Would have saved: " + data);
OrderService testService = new OrderService(fakeDatabase);
testService.placeOrder("Test order");
```

### 4.4 Explanation

Two changes were made:

1. **`OrderService` depends on the `Database` interface**, not on a concrete class. It no longer cares what kind of database is behind it.
2. **The dependency is received through the constructor**, instead of being created inside the class. This technique is called *dependency injection*.

The "inversion" in the principle's name refers to the direction of the dependency. Before, the application logic depended on the concrete detail (MySQL). Now, both `OrderService` and `MySQLDatabase` depend on the `Database` abstraction. The concrete detail adapts to the interface, not the other way around.

The practical benefits can be seen in the usage example: the database can be switched with a single line, without modifying `OrderService`, and in tests a fake implementation (`fakeDatabase`) can be injected that does not need a real server. Popular frameworks such as Spring (Java) and ASP.NET Core (C#) are built around this principle.

---

## 5. Execution Results

The examples above are part of the Java project in this folder in which each principle has a `bad` package (the violation) and a `good` package (the correct version). Running the `Main` class produces the following output:

```
=== SRP: Single Responsibility Principle ===
--- Before (one class does everything) ---
Total: 120.0
Invoice saved to invoice.txt (total: 120.0)
Invoice sent to client@example.com
--- After (separate responsibilities) ---
Total: 120.0
Invoice saved to invoice.txt (total: 120.0)
Invoice sent to client@example.com

=== OCP: Open/Closed Principle ===
--- Before (if/else chain) ---
REGULAR: 95.0
VIP: 80.0
STUDENT: 90.0
PENSIONER: 100.0  <- new type not supported without modifying the class
--- After (polymorphism through the Discount interface) ---
RegularDiscount: 95.0
VipDiscount: 80.0
StudentDiscount: 90.0
PensionerDiscount: 85.0

=== DIP: Dependency Inversion Principle ===
--- Before (concrete dependency, created with new) ---
Saved to MySQL: Order #1
--- After (dependency injected through the constructor) ---
Saved to MySQL: Order #1
Saved to MongoDB: Order #2
[TEST] Would have saved: Test order
```

For SRP, the output is identical in both versions. The principle does not change *what* the program does, but *how* the code is organized. For OCP, the old version cannot apply the pensioner discount (the price stays at 100.0), while the new version supports it through a single added class. For DIP, the same `OrderService` works with three different database implementations, including a fake one for testing.

---

## 6. Conclusion

The SOLID principles are not syntax rules, but design guidelines that help produce code that is easier to maintain in the long run. The three principles analyzed solve different problems, but complement each other:

- **SRP** splits code into small classes, each with a single responsibility, so a change affects only one place;
- **OCP** allows new functionality to be added without modifying and retesting existing code;
- **DIP** reduces coupling between components, which makes it possible to swap implementations and test them in isolation.

The examples share a common element: all three rely on separating responsibilities and on using abstractions (interfaces) instead of direct dependencies on concrete classes. The result is code that is more flexible, easier to test and more resilient to change.

However, the principles should be applied with judgment. In a small program, excessive splitting into classes and interfaces can complicate the code unnecessarily. Their value becomes clear as the project grows, the team expands and requirements change, which are exactly the situations found in real-world application development.

---

## How to Run

Requirements: JDK 17 or newer (Maven is optional; IntelliJ IDEA handles it automatically).

**IntelliJ IDEA:** File → Open… → select the `lab0` folder (or its `pom.xml`) → open `src/main/java/solid/Main.java` → click the green arrow next to `main`.

**Command line:**

```bash
cd lab0
mkdir -p out
javac -d out $(find src -name "*.java")
java -cp out solid.Main
```

Each principle also has its own runnable demo: `solid.srp.SrpDemo`, `solid.ocp.OcpDemo`, `solid.dip.DipDemo`.

### Project structure

```
lab0/
├── pom.xml
├── report/
│   └── Report_SOLID.docx     the report in Word format
└── src/main/java/solid/
    ├── Main.java             runs all demos
    ├── srp/
    │   ├── bad/              Invoice that does everything
    │   ├── good/             Invoice, InvoiceRepository, EmailService
    │   └── SrpDemo.java
    ├── ocp/
    │   ├── bad/              DiscountCalculator with if/else
    │   ├── good/             Discount interface + implementations
    │   └── OcpDemo.java
    └── dip/
        ├── bad/              OrderService glued to MySQLDatabase
        ├── good/             Database interface + constructor injection
        └── DipDemo.java
```

In each principle, the `bad` package contains the version that violates the principle and `good` contains the version that follows it.
