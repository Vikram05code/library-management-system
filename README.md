# Library Management System

A fully-featured Library Management System implemented in Java, demonstrating Object-Oriented Programming, SOLID principles, and Gang-of-Four design patterns.

---

## Features

### Core
- **Book Management** — add, remove, update books; search by title, author, ISBN, or genre
- **Patron Management** — register patrons, update info, full borrowing history
- **Lending Process** — check out and return books with due-date tracking
- **Inventory Management** — per-branch tracking of available vs. borrowed books

### Extended
- **Multi-Branch Support** — multiple library branches, each with its own inventory
- **Book Transfers** — move available books between branches
- **Reservation System** — FIFO queue per book; reservations expire after 7 days
- **Notification System** — patrons notified when reserved books become available (Observer pattern)
- **Recommendation Engine** — personalised recommendations based on genre preferences and global popularity

---

## Project Structure

```
src/main/java/com/library/
├── Main.java                          # Entry point / demo
├── LibrarySystem.java                 # Facade — wires all services together
│
├── model/
│   ├── Book.java                      # Book entity (ISBN, title, author, genre, status)
│   ├── Patron.java                    # Patron entity (id, name, borrowing history)
│   ├── LendingRecord.java             # Single lending transaction
│   ├── LibraryBranch.java             # Branch with its own book inventory
│   └── Reservation.java              # Book reservation by a patron
│
├── service/
│   ├── BookService.java               # Book CRUD + search
│   ├── PatronService.java             # Patron CRUD + history
│   ├── BranchService.java             # Multi-branch management + transfers
│   ├── LendingService.java            # Checkout + return + overdue tracking
│   ├── ReservationService.java        # Reservation queue management
│   └── RecommendationService.java     # Genre-based recommendation algorithm
│
├── repository/
│   ├── BookRepository.java            # Interface
│   ├── InMemoryBookRepository.java    # HashMap-backed implementation
│   ├── PatronRepository.java          # Interface
│   ├── InMemoryPatronRepository.java
│   ├── LendingRepository.java         # Interface
│   ├── InMemoryLendingRepository.java
│   ├── ReservationRepository.java     # Interface (FIFO via LinkedHashMap)
│   └── InMemoryReservationRepository.java
│
├── pattern/
│   ├── observer/
│   │   ├── BookEvent.java             # Event enum
│   │   ├── LibraryObserver.java       # Observer interface
│   │   ├── EventPublisher.java        # Subject — dispatches events
│   │   └── NotificationService.java   # Concrete observer — patron notifications
│   │
│   ├── factory/
│   │   └── BookFactory.java           # Centralises Book construction
│   │
│   └── strategy/
│       ├── SearchStrategy.java        # Strategy interface
│       └── SearchStrategyFactory.java # Concrete strategies + factory accessor
│
└── exception/
    ├── BookNotFoundException.java
    ├── PatronNotFoundException.java
    ├── BookNotAvailableException.java
    └── BranchNotFoundException.java
```

---

## Class Diagram

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           LIBRARY MANAGEMENT SYSTEM                         │
└─────────────────────────────────────────────────────────────────────────────┘

  ┌────────────────────┐       ┌────────────────────┐      ┌──────────────────┐
  │      Book          │       │      Patron         │      │  LendingRecord   │
  ├────────────────────┤       ├────────────────────┤      ├──────────────────┤
  │ - isbn: String     │       │ - patronId: String  │      │ - recordId       │
  │ - title: String    │       │ - name: String      │      │ - patronId       │
  │ - author: String   │       │ - email: String     │      │ - isbn           │
  │ - year: int        │       │ - phone: String     │      │ - branchId       │
  │ - genre: Genre     │       │ - history: List     │◄─────│ - checkoutDate   │
  │ - status: Status   │       │ - reservations: List│      │ - dueDate        │
  ├────────────────────┤       ├────────────────────┤      │ - returnDate     │
  │ + isAvailable()    │       │ + addLendingRecord()│      ├──────────────────┤
  │ + setStatus()      │       │ + getCurrentLoans() │      │ + isReturned()   │
  └────────────────────┘       └────────────────────┘      │ + isOverdue()    │
           ▲                            ▲                   └──────────────────┘
           │                            │
  ┌────────────────────┐       ┌────────────────────┐
  │   LibraryBranch    │       │    Reservation      │
  ├────────────────────┤       ├────────────────────┤
  │ - branchId: String │       │ - reservationId     │
  │ - name: String     │       │ - patronId          │
  │ - inventory: Map   │       │ - isbn              │
  ├────────────────────┤       │ - branchId          │
  │ + addBook()        │       │ - status: Status    │
  │ + removeBook()     │       │ - expiryDate        │
  │ + findBookByIsbn() │       ├────────────────────┤
  │ + getAvailable()   │       │ + isActive()        │
  └────────────────────┘       │ + isExpired()       │
                               └────────────────────┘

═══════════════════════ SERVICE LAYER ═══════════════════════

  ┌────────────────────┐  uses  ┌────────────────────┐
  │   BookService      │───────►│   BookRepository   │ (interface)
  ├────────────────────┤        └────────────────────┘
  │ + addBook()        │              ▲
  │ + removeBook()     │              │ implements
  │ + updateBook()     │    ┌─────────────────────────┐
  │ + search()         │    │  InMemoryBookRepository  │
  └────────────────────┘    └─────────────────────────┘

  ┌────────────────────┐        ┌────────────────────┐
  │   LendingService   │───────►│  LendingRepository │
  ├────────────────────┤        └────────────────────┘
  │ + checkout()       │
  │ + returnBook()     │◄── uses ── EventPublisher
  │ + getOverdue()     │◄── uses ── ReservationService
  └────────────────────┘

  ┌────────────────────┐        ┌────────────────────┐
  │  BranchService     │        │  PatronService      │
  ├────────────────────┤        ├────────────────────┤
  │ + addBranch()      │        │ + addPatron()       │
  │ + transferBook()   │        │ + updatePatron()    │
  └────────────────────┘        │ + getBorrowHistory()│
                                └────────────────────┘

  ┌────────────────────┐        ┌────────────────────┐
  │ ReservationService │        │RecommendationService│
  ├────────────────────┤        ├────────────────────┤
  │ + placeReservation │        │ + recommend()       │
  │ + cancelReservation│        │  (genre + popularity│
  │ + notifyNext()     │        │   weighted scoring) │
  └────────────────────┘        └────────────────────┘

═══════════════════════ DESIGN PATTERNS ═══════════════════════

  ┌──────────────────────────────────────────────────────────┐
  │                    OBSERVER PATTERN                       │
  │                                                           │
  │  EventPublisher (Subject)                                 │
  │      │ publish(event, isbn, patronId, branchId)           │
  │      ▼                                                    │
  │  LibraryObserver ◄──── NotificationService               │
  │   (interface)           (sends patron notifications)      │
  │      «BookEvent enum»                                     │
  │       BOOK_RETURNED | BOOK_AVAILABLE | BOOK_OVERDUE       │
  │       RESERVATION_EXPIRED | BOOK_TRANSFERRED              │
  └──────────────────────────────────────────────────────────┘

  ┌──────────────────────────────────────────────────────────┐
  │                    FACTORY PATTERN                        │
  │                                                           │
  │  BookFactory                                              │
  │  + createBook(isbn, title, author, year, genre) : Book    │
  │  + createBook(isbn, title, author, year) : Book           │
  │  + createCopy(original) : Book                            │
  └──────────────────────────────────────────────────────────┘

  ┌──────────────────────────────────────────────────────────┐
  │                   STRATEGY PATTERN                        │
  │                                                           │
  │  SearchStrategy (interface)                               │
  │    + search(books, query) : List<Book>                    │
  │         ▲                                                 │
  │         │ implements                                      │
  │  ┌──────┴─────────────────────────────────┐              │
  │  │              │              │           │              │
  │  TitleSearch  AuthorSearch  IsbnSearch  GenreSearch       │
  │                                                           │
  │  SearchStrategyFactory.of(SearchType) → SearchStrategy    │
  └──────────────────────────────────────────────────────────┘

  ┌──────────────────────────────────────────────────────────┐
  │                    FACADE PATTERN                         │
  │                                                           │
  │  LibrarySystem (Facade)                                   │
  │    + addBranch() + addBook() + registerPatron()           │
  │    + checkout()  + returnBook() + reserveBook()           │
  │    + transferBook() + getRecommendations()                │
  │    Hides: repository wiring, event setup, service deps    │
  └──────────────────────────────────────────────────────────┘
```

---

## SOLID Principles Applied

| Principle | How it's applied |
|-----------|-----------------|
| **Single Responsibility** | Each service class has exactly one job: `BookService` manages books, `LendingService` manages lending, etc. |
| **Open/Closed** | New search strategies can be added without modifying `BookService`. New observers can be added without touching `EventPublisher`. |
| **Liskov Substitution** | `InMemoryBookRepository` fully substitutes `BookRepository`; any future JPA implementation would too. |
| **Interface Segregation** | Repository interfaces are small and focused (`BookRepository`, `PatronRepository`, etc.) rather than one large interface. |
| **Dependency Inversion** | Services depend on repository *interfaces*, not concrete implementations. `EventPublisher` depends on `LibraryObserver`, not on `NotificationService`. |

---

## Design Patterns

| Pattern | Where | Purpose |
|---------|-------|---------|
| **Observer** | `EventPublisher` / `LibraryObserver` / `NotificationService` | Decouple event publishing from notification handling; easily add new observers |
| **Factory** | `BookFactory` | Centralise and standardise Book object creation |
| **Strategy** | `SearchStrategy` / `SearchStrategyFactory` | Swap search algorithms at runtime without changing `BookService` |
| **Facade** | `LibrarySystem` | Single entry point that hides internal wiring complexity |
| **Repository** | `*Repository` interfaces + in-memory implementations | Decouple persistence from business logic; swap to JPA with zero service changes |

---

## Getting Started

### Prerequisites
- Java 21+
- Maven 3.8+

### Build & Run

```bash
# Clone the repository
git clone <your-repo-url>
cd library-management-system

# Build and run tests
mvn clean verify

# Run the demo application
mvn exec:java -Dexec.mainClass="com.library.Main"

# Or build an executable JAR and run it
mvn clean package
java -jar target/library-management-system-1.0.0.jar
```

### Run Tests Only

```bash
mvn test
```

---

## Technology Choices

| Choice | Rationale |
|--------|-----------|
| **Java 21** | Pattern matching, records, sealed classes, text blocks |
| **SLF4J + Logback** | Industry-standard logging; SLF4J abstracts the implementation |
| **JUnit 5** | Modern test annotations; parameterised tests |
| **Maven Shade** | Produces a fat JAR for easy distribution |
| **In-memory repositories** | Keeps focus on OOP; swap to JPA/JDBC by implementing the same interfaces |

---

## Extending the System

### Add a new search strategy
1. Implement `SearchStrategy` in a new class (e.g., `YearRangeSearchStrategy`)
2. Add an entry to `SearchStrategyFactory.SearchType`
3. Handle it in the `SearchStrategyFactory.of()` switch — no other changes needed

### Add a new notification channel (e.g., SMS)
1. Create `SmsNotificationService implements LibraryObserver`
2. Register it: `eventPublisher.subscribe(new SmsNotificationService(...))`
3. `EventPublisher` automatically delivers events to it

### Persist to a database
1. Create `JpaBookRepository implements BookRepository`
2. Inject it into `LibrarySystem` — all services work unchanged
