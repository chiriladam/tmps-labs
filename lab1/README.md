# Creational Design Patterns

## Author: Macari Chiril-Adam, FAF-243

**Course:** Techniques and Mechanisms of Software Design<br>
**Laboratory Work No.1:** Creational Design Patterns<br>
**Domain:** Hotel booking system, implemented in Java (no frameworks or external libraries)

---

## 1. Objectives

- Get familiar with the Creational Design Patterns;
- Choose a specific domain, define its main classes and the instantiation mechanisms they need;
- Implement at least 3 Creational Design Patterns for the chosen domain.

---

## 2. Theoretical Background

### 2.1 Overview

Creational design patterns deal with **object instantiation**. Instead of creating objects directly with `new` all over the code, they move the creation logic into dedicated places, so the rest of the program does not depend on how, when or which concrete objects are created. This makes the code more flexible: the type of object, the way it is assembled or the number of instances can change without modifying the code that uses it.

The creational patterns are usually divided into two groups:

- **Class-creation patterns** use **inheritance** to decide which class gets instantiated. A subclass overrides a method to choose the concrete type (Factory Method).
- **Object-creation patterns** use **delegation**: the creation is handed over to another object (Abstract Factory, Builder, Prototype, Singleton, Object Pool).

| Pattern | Group | Main idea |
|---|---|---|
| **Singleton** | Object | Exactly one instance of a class, with a global access point |
| **Factory Method** | Class | Subclasses decide which concrete class to instantiate |
| **Abstract Factory** | Object | Creates whole families of related objects |
| **Builder** | Object | Builds a complex object step by step |
| **Prototype** | Object | Creates new objects by cloning an existing one |
| **Object Pool** | Object | Reuses a set of already created, expensive objects |

### 2.2 Singleton

**Intent.** Ensure that a class has only one instance at any time and provide a global point of access to it.

**Structure.** The class keeps a private static field with its only instance, has a **private constructor** (so no one else can call `new`) and exposes a static method, usually `getInstance()`, that creates the instance the first time and returns the same one afterwards.

**Thread safety.** If two threads call `getInstance()` at the same moment, a naive implementation can create two instances. Common thread-safe variants in Java:

- **Eager initialization:** the instance is created when the class is loaded (`private static final Singleton INSTANCE = new Singleton();`). Simple, but the object is created even if never used.
- **Synchronized method:** `getInstance()` is `synchronized`. Correct, but every call pays the cost of locking.
- **Double-checked locking:** checks the instance once without a lock and once inside a `synchronized` block; the field must be `volatile`. Lazy and fast after the first call.
- **Holder class (Bill Pugh):** a static nested class holds the instance; the JVM loads it only on first use, which is lazy and thread-safe without explicit locks.
- **Enum singleton:** `enum Singleton { INSTANCE; }`. Thread-safe and also safe against serialization and reflection.

**When to use.** Shared resources that must exist exactly once: configuration, logging, caches, a connection manager, a registry.

**Drawbacks.** It introduces global state, hides dependencies and makes unit testing harder, so it should be used only when "exactly one" is a real requirement.

**In the hotel domain.** The hotel has one booking system. If every reception desk had its own copy, two desks could sell the same room for the same night.

### 2.3 Factory Method

**Intent.** Define an interface for creating an object, but let subclasses decide which class to instantiate.

**Structure.** *Product* (common interface), *Concrete Products*, *Creator* (declares the abstract factory method and usually contains logic that uses the product) and *Concrete Creators* (override the factory method to return a specific product).

**When to use.** When a class cannot know in advance which concrete objects it must create, or when the creation of a product should be extended later by adding new subclasses instead of changing existing code (Open/Closed Principle).

**Pros and cons.** Removes the dependency of the client on concrete classes and keeps creation code in one place, but every new product needs a new creator subclass.

**In the hotel domain.** Each room type (single, double, suite) has its own factory. Opening a new room type means adding a new factory, without changing the code that opens rooms.

### 2.4 Abstract Factory

**Intent.** Provide an interface for creating **families of related or dependent objects** without specifying their concrete classes.

**Structure.** An *Abstract Factory* interface declares one creation method per product type (each of them is a factory method). *Concrete Factories* implement it for one family. The client works only with the abstract factory and the abstract products, so the products it receives always belong to the same family.

**When to use.** When the system must work with several families of products that have to be used together (for example, UI widgets for Windows vs macOS) and the family can be switched as a whole.

**Pros and cons.** Guarantees consistent families and makes switching families easy, but adding a new *kind* of product requires changing the abstract factory and all of its implementations.

**Difference from Factory Method.** Factory Method creates **one** product and relies on inheritance; Abstract Factory creates a **family** of products and relies on composition (the client receives a factory object).

**In the hotel domain.** A `RoomThemeFactory` could create matching sets of furniture, decorations and welcome packs for a "Classic" or a "Modern" room theme.

### 2.5 Builder

**Intent.** Separate the construction of a complex object from its representation, so that the same construction process can create different representations.

**Structure.** *Builder* (step-by-step construction methods), *Concrete Builder* (assembles the parts and returns the result), *Director* (knows the order of steps for common configurations) and *Product* (the complex object). In Java it is often implemented as a static nested `Builder` class with chained ("fluent") methods and a final `build()` call.

**When to use.** When an object has many parameters, most of them optional, and a constructor with a long list of arguments ("telescoping constructor") would be hard to read and easy to misuse. It also allows validating the object once, before it is created, and makes the result immutable.

**Pros and cons.** Readable creation code, immutable objects and validation in one place, at the cost of an extra class.

**In the hotel domain.** A reservation has 4 required fields (guest, room, check-in, check-out) and many optional ones (number of guests, breakfast, parking, airport transfer, special requests).

### 2.6 Prototype

**Intent.** Create new objects by **copying an existing object** (the prototype) instead of building them from scratch.

**Structure.** A *Prototype* interface declares a `clone()` method; *Concrete Prototypes* implement it. Often a *prototype registry* (a collection that caches ready-made prototypes) returns copies on request. In Java this can be done with `Cloneable` and `clone()`, or more safely with a copy constructor.

**Shallow vs deep copy.** A shallow copy duplicates only the object itself, so it shares the referenced objects (lists, nested objects) with the original; a deep copy duplicates them too. Choosing the wrong one leads to copies that unexpectedly change together.

**When to use.** When creating an object is expensive (loading data, complex setup) or when the system needs many objects that differ only slightly from a few preconfigured ones.

**In the hotel domain.** A preconfigured "conference package" (meeting room, coffee breaks, projector) could be cloned and then slightly adjusted for each company.

### 2.7 Object Pool

**Intent.** Keep a set of initialized objects ready to use, instead of creating and destroying them on demand. A client **acquires** an object from the pool, uses it and **releases** it back.

**When to use.** When objects are expensive to create and are needed often but only for a short time, and the number of objects in use at the same time is limited. Classic examples are database connection pools and thread pools.

**Things to watch.** Objects must be reset to a clean state when they are returned; the pool must be thread-safe; and a client that never releases an object makes the pool run empty.

**In the hotel domain.** A limited number of shuttle buses or key-card encoders can be modelled as a pool: a reception desk takes one, uses it and gives it back.

### 2.8 Choosing a Pattern

| Problem | Pattern |
|---|---|
| Only one instance may exist and everyone must share it | Singleton |
| The concrete class to create should be decided by subclasses | Factory Method |
| Several related objects must be created as a consistent family | Abstract Factory |
| The object has many optional parts or needs validation before creation | Builder |
| New objects should be copies of a preconfigured object | Prototype |
| Expensive objects should be reused instead of re-created | Object Pool |

The patterns are often combined. For example, an Abstract Factory is usually implemented with Factory Methods, and factories or builders are frequently Singletons.

---

## 3. Used Design Patterns

- **Singleton** — `BookingSystem`, the single shared registry of rooms and reservations;
- **Factory Method** — `RoomFactory` and its subclasses `SingleRoomFactory`, `DoubleRoomFactory`, `SuiteFactory`;
- **Builder** — `Reservation.Builder` together with the `ReservationDirector`.

---

## 4. Implementation

### 4.1 Domain and Project Structure

The project models a small hotel. Rooms of different types are opened through factories, all rooms and reservations are stored in one booking system, and reservations with optional extras are created with a builder. The packages are separated by responsibility, as required: `client` contains the program that uses the domain, and `domain` contains the models, the factories, the builder director and the booking service.

```
src/main/java/hotel/
├── client/
│   └── Main.java                  demo: uses all three patterns
└── domain/
    ├── models/
    │   ├── Room.java              abstract product
    │   ├── SingleRoom.java        concrete products
    │   ├── DoubleRoom.java
    │   ├── Suite.java
    │   ├── RoomType.java
    │   └── Reservation.java       product + nested Builder
    ├── factory/
    │   ├── RoomFactory.java       Factory Method: abstract creator
    │   ├── SingleRoomFactory.java concrete creators
    │   ├── DoubleRoomFactory.java
    │   └── SuiteFactory.java
    ├── builder/
    │   └── ReservationDirector.java  Builder: director with preset packages
    └── service/
        └── BookingSystem.java     Singleton
```

### 4.2 Singleton — BookingSystem

`BookingSystem` stores every room and reservation of the hotel. Its constructor is private, and the only way to get the object is `getInstance()`, which uses **double-checked locking** with a `volatile` field. That makes the creation thread-safe while avoiding a lock on every call after the instance exists.

```java
public final class BookingSystem {

    // volatile: every thread sees the fully constructed instance
    private static volatile BookingSystem instance;

    private final Map<Integer, Room> rooms = new LinkedHashMap<>();
    private final List<Reservation> reservations = new ArrayList<>();

    // private constructor: nobody outside can call "new BookingSystem()"
    private BookingSystem() {
        System.out.println("[BookingSystem] Single instance created");
    }

    // thread-safe lazy initialization with double-checked locking
    public static BookingSystem getInstance() {
        if (instance == null) {
            synchronized (BookingSystem.class) {
                if (instance == null) {
                    instance = new BookingSystem();
                }
            }
        }
        return instance;
    }
```

Because there is only one booking system, it can enforce a rule that would be impossible with several copies: a room cannot be booked twice for overlapping dates. Its methods are `synchronized`, so two desks booking at the same time cannot both get the same room.

```java
public synchronized boolean book(Reservation reservation) {
    if (!isAvailable(reservation.getRoom(), reservation.getCheckIn(), reservation.getCheckOut())) {
        return false;
    }
    reservations.add(reservation);
    return true;
}
```

The client checks thread safety by starting 5 threads ("reception desks") that call `getInstance()` at the same time and collecting the objects they receive:

```java
// 5 reception desks ask for the booking system at the same time
Set<BookingSystem> seen = ConcurrentHashMap.newKeySet();
List<Thread> desks = new ArrayList<>();
for (int i = 0; i < 5; i++) {
    desks.add(new Thread(() -> seen.add(BookingSystem.getInstance())));
}
for (Thread desk : desks) desk.start();
for (Thread desk : desks) desk.join();
System.out.println("Distinct instances seen by 5 threads: " + seen.size());
```

### 4.3 Factory Method — RoomFactory

`RoomFactory` is the abstract creator. It declares the factory method `createRoom()` and contains the common logic in `openRoom()`, which creates a room and registers it in the booking system. `openRoom()` works only with the abstract `Room`, so it never needs to know which concrete class was created. That decision belongs to the subclasses.

```java
public abstract class RoomFactory {

    // the factory method
    protected abstract Room createRoom(int number);

    public Room openRoom(int number) {
        Room room = createRoom(number);
        BookingSystem.getInstance().addRoom(room);
        return room;
    }
}
```

Each concrete creator overrides only the factory method and returns its own type of room:

```java
public class SuiteFactory extends RoomFactory {
    @Override
    protected Room createRoom(int number) {
        return new Suite(number);
    }
}
```

The client picks a factory by room type and uses it through the `RoomFactory` abstraction, without referring to `SingleRoom`, `DoubleRoom` or `Suite` directly. Adding a new room type (for example, a family room) means adding one product class and one factory, with no changes to `RoomFactory` or to the existing factories.

```java
Map<RoomType, RoomFactory> factories = Map.of(
        RoomType.SINGLE, new SingleRoomFactory(),
        RoomType.DOUBLE, new DoubleRoomFactory(),
        RoomType.SUITE, new SuiteFactory());

// the client only knows RoomFactory, not the concrete room classes
factories.get(RoomType.SINGLE).openRoom(101);
factories.get(RoomType.SINGLE).openRoom(102);
factories.get(RoomType.DOUBLE).openRoom(201);
factories.get(RoomType.DOUBLE).openRoom(202);
factories.get(RoomType.SUITE).openRoom(301);
```

This also shows how creational patterns work together: the Factory Method registers every new room in the Singleton.

### 4.4 Builder — Reservation.Builder and ReservationDirector

A `Reservation` has 4 required fields and 5 optional ones. Its constructor is **private** and takes a `Builder`, so a reservation can only be created through the builder. The required fields are passed to the builder's constructor, the optional ones are set with chained methods, and `build()` validates the data before the object is created.

```java
// private: the only way to create a Reservation is through the Builder
private Reservation(Builder builder) {
    this.guestName = builder.guestName;
    this.room = builder.room;
    this.checkIn = builder.checkIn;
    this.checkOut = builder.checkOut;
    this.guests = builder.guests;
    this.breakfast = builder.breakfast;
    this.parking = builder.parking;
    this.airportTransfer = builder.airportTransfer;
    this.specialRequest = builder.specialRequest;
}
```

```java
public Builder withBreakfast() {
    this.breakfast = true;
    return this;
}
```

```java
public Reservation build() {
    if (!checkOut.isAfter(checkIn)) {
        throw new IllegalStateException("Check-out must be after check-in");
    }
    if (guests < 1 || guests > room.getCapacity()) {
        throw new IllegalStateException("Room " + room.getNumber() + " (" + room.getType()
                + ") fits at most " + room.getCapacity() + " guest(s), requested " + guests);
    }
    return new Reservation(this);
}
```

Thanks to the builder, the client code reads almost like a sentence and only mentions the options it actually needs:

```java
Reservation custom = new Reservation.Builder("Maria Popescu", double201, nov10, nov13)
        .guests(2)
        .withBreakfast()
        .withParking()
        .specialRequest("Room on a high floor")
        .build();
```

The `ReservationDirector` plays the role of the **Director** from the classic Builder structure. It knows the construction steps for the hotel's standard packages, so the same builder produces different representations (a weekend getaway, a business trip) without the client repeating the steps:

```java
public Reservation weekendGetaway(String guestName, Room room, LocalDate friday) {
    return new Reservation.Builder(guestName, room, friday, friday.plusDays(2))
            .guests(2)
            .withBreakfast()
            .specialRequest("Late check-out")
            .build();
}

public Reservation businessTrip(String guestName, Room room, LocalDate checkIn, int nights) {
    return new Reservation.Builder(guestName, room, checkIn, checkIn.plusDays(nights))
            .withBreakfast()
            .withParking()
            .withAirportTransfer()
            .build();
}
```

---

## 5. Results

Running `hotel.client.Main` produces the following output:

```
=== 1. Singleton: BookingSystem ===
[BookingSystem] Single instance created
Distinct instances seen by 5 threads: 1
getInstance() == getInstance(): true

=== 2. Factory Method: opening rooms ===
Room 101 (SINGLE) - 60 EUR/night, up to 1 guest(s), Wi-Fi, Work desk
Room 102 (SINGLE) - 60 EUR/night, up to 1 guest(s), Wi-Fi, Work desk
Room 201 (DOUBLE) - 90 EUR/night, up to 2 guest(s), Wi-Fi, Queen bed, City view
Room 202 (DOUBLE) - 90 EUR/night, up to 2 guest(s), Wi-Fi, Queen bed, City view
Room 301 (SUITE) - 180 EUR/night, up to 4 guest(s), Wi-Fi, King bed, Living room, Jacuzzi

=== 3. Builder: creating reservations ===
BOOKED   Maria Popescu | room 201 (DOUBLE) | 2026-11-10 -> 2026-11-13 (3 nights) | 2 guest(s) | extras: breakfast parking | request: Room on a high floor | total: 390 EUR
BOOKED   Ion Rusu | room 301 (SUITE) | 2026-11-13 -> 2026-11-15 (2 nights) | 2 guest(s) | extras: breakfast | request: Late check-out | total: 420 EUR
BOOKED   Elena Ciobanu | room 101 (SINGLE) | 2026-11-10 -> 2026-11-12 (2 nights) | 1 guest(s) | extras: breakfast parking airport-transfer | request: - | total: 200 EUR
Invalid reservation not built: Room 101 (SINGLE) fits at most 1 guest(s), requested 3
REJECTED Victor Moraru | room 301 (SUITE) | 2026-11-14 -> 2026-11-16 (2 nights) | 1 guest(s) | extras: none | request: - | total: 360 EUR
Total reservations: 3, total revenue: 1010 EUR
```

The output confirms that each pattern behaves as intended:

- **Singleton:** five threads requested the booking system at the same time, yet the constructor ran only once and all of them received the same object.
- **Factory Method:** five rooms of three types were created through the `RoomFactory` abstraction and registered in the booking system, each with the price, capacity and amenities of its type.
- **Builder:** one custom reservation and two preset packages from the Director were built and booked, with totals computed from the chosen extras (for example, 3 nights × 90 EUR + breakfast 3 × 2 × 15 EUR + parking 3 × 10 EUR = 390 EUR). The builder refused to create a reservation for 3 guests in a single room.
- **Patterns working together:** the booking for the suite on 14–16 November was rejected because it overlaps the weekend package. This works only because every part of the program shares the same `BookingSystem` instance.

---

## 6. Conclusions

In this laboratory work I studied the creational design patterns and implemented three of them in a hotel booking system. Each pattern solved a concrete problem of the domain:

- **Singleton** guarantees that the whole application works with one booking system, which keeps all rooms and reservations in one place and prevents double bookings. Making it thread-safe with double-checked locking was essential, since several reception desks can access it at the same time.
- **Factory Method** removed the dependency of the client on concrete room classes. New room types can be added by writing a new factory, without touching the existing code, which also follows the Open/Closed Principle from the previous laboratory work.
- **Builder** made it possible to create reservations with many optional parameters in a readable way, to validate them before creation and to keep them immutable. The Director added reusable presets for the hotel's standard packages.

The patterns also complement each other: the factories register their rooms in the Singleton, and the builder creates reservations for rooms that the factories produced. The remaining patterns — Abstract Factory, Prototype and Object Pool — solve related problems (families of objects, cloning, and reusing expensive objects). They were described in the theoretical part with examples of how they could extend this project.

Overall, creational patterns move the responsibility of *creating* objects away from the code that *uses* them, which results in code that is easier to extend, test and maintain.

---

## How to Run

Requirements: JDK 17 or newer. No frameworks or external libraries are used.

**IntelliJ IDEA:** File → Open… → select the `lab1` folder (or its `pom.xml`) → open `src/main/java/hotel/client/Main.java` → click the green arrow next to `main`.

**Command line:**

```bash
cd lab1
mkdir -p out
javac -d out $(find src -name "*.java")
java -cp out hotel.client.Main
```

The report is also available in Word format: [`report/Report_Lab1.docx`](report/Report_Lab1.docx).

---

## References

- Vasile Drumea, *Creational Design Patterns*, lecture slides, Software Design Techniques and Mechanisms, Technical University of Moldova.
- E. Gamma, R. Helm, R. Johnson, J. Vlissides, *Design Patterns: Elements of Reusable Object-Oriented Software*, Addison-Wesley, 1994.
- SourceMaking, *Creational patterns*: https://sourcemaking.com/design_patterns/creational_patterns
