package com.library;

import com.library.exception.BookNotAvailableException;
import com.library.model.*;
import com.library.pattern.factory.BookFactory;
import com.library.pattern.observer.EventPublisher;
import com.library.repository.*;
import com.library.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LendingServiceTest {

    private LendingServiceImpl lendingService;
    private BookServiceImpl bookService;
    private PatronServiceImpl patronService;
    private ReservationServiceImpl reservationService;
    private static final String BRANCH_ID = "B001";

    @BeforeEach
    void setUp() {
        BranchServiceImpl branchService = new BranchServiceImpl();
        LibraryBranch branch = new LibraryBranch(BRANCH_ID, "Test Branch", "1 Test St");
        branchService.addBranch(branch);

        EventPublisher eventPublisher = new EventPublisher();
        patronService      = new PatronServiceImpl(new InMemoryPatronRepository());
        bookService        = new BookServiceImpl(new InMemoryBookRepository(), branchService);
        reservationService = new ReservationServiceImpl(new InMemoryReservationRepository(), patronService, eventPublisher);

        lendingService = new LendingServiceImpl(
                new InMemoryLendingRepository(),
                bookService, patronService, branchService,
                eventPublisher, reservationService);

        // Seed data
        Book book = BookFactory.createBook("ISBN-001", "Dune", "Frank Herbert", 1965, Book.Genre.FICTION);
        bookService.addBook(book, BRANCH_ID);

        Patron patron = new Patron("P001", "Alice", "alice@test.com", "555-0001");
        patronService.addPatron(patron);
    }

    @Test
    void checkout_setsBookStatusToBorrowed() {
        lendingService.checkout("ISBN-001", "P001", BRANCH_ID);

        Book book = bookService.findByIsbnOrThrow("ISBN-001");
        assertEquals(Book.BookStatus.BORROWED, book.getStatus());
    }

    @Test
    void checkout_returnsLendingRecordWithCorrectFields() {
        LendingRecord record = lendingService.checkout("ISBN-001", "P001", BRANCH_ID);

        assertNotNull(record.getRecordId());
        assertEquals("P001", record.getPatronId());
        assertEquals("ISBN-001", record.getIsbn());
        assertNull(record.getReturnDate());
    }

    @Test
    void checkout_whenBookAlreadyBorrowed_throwsException() {
        lendingService.checkout("ISBN-001", "P001", BRANCH_ID);

        assertThrows(BookNotAvailableException.class,
                () -> lendingService.checkout("ISBN-001", "P001", BRANCH_ID));
    }

    @Test
    void returnBook_setsBookStatusToAvailable() {
        lendingService.checkout("ISBN-001", "P001", BRANCH_ID);
        lendingService.returnBook("ISBN-001", "P001", BRANCH_ID);

        Book book = bookService.findByIsbnOrThrow("ISBN-001");
        assertEquals(Book.BookStatus.AVAILABLE, book.getStatus());
    }

    @Test
    void returnBook_setsReturnDate() {
        lendingService.checkout("ISBN-001", "P001", BRANCH_ID);
        LendingRecord returned = lendingService.returnBook("ISBN-001", "P001", BRANCH_ID);

        assertNotNull(returned.getReturnDate());
        assertTrue(returned.isReturned());
    }

    @Test
    void returnBook_withReservation_setsStatusToReserved() {
        Patron bob = new Patron("P002", "Bob", "bob@test.com", "555-0002");
        patronService.addPatron(bob);

        lendingService.checkout("ISBN-001", "P001", BRANCH_ID);
        reservationService.placeReservation("ISBN-001", "P002", BRANCH_ID);
        lendingService.returnBook("ISBN-001", "P001", BRANCH_ID);

        Book book = bookService.findByIsbnOrThrow("ISBN-001");
        assertEquals(Book.BookStatus.RESERVED, book.getStatus());
    }

    @Test
    void getLendingHistoryByPatron_includesAllRecords() {
        lendingService.checkout("ISBN-001", "P001", BRANCH_ID);
        lendingService.returnBook("ISBN-001", "P001", BRANCH_ID);
        lendingService.checkout("ISBN-001", "P001", BRANCH_ID);

        var history = lendingService.getLendingHistoryByPatron("P001");
        assertEquals(2, history.size());
    }
}
