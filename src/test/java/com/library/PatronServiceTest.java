package com.library;

import com.library.exception.PatronNotFoundException;
import com.library.model.Patron;
import com.library.repository.InMemoryPatronRepository;
import com.library.service.PatronServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PatronServiceTest {

    private PatronServiceImpl patronService;

    @BeforeEach
    void setUp() {
        patronService = new PatronServiceImpl(new InMemoryPatronRepository());
    }

    @Test
    void addPatron_successfullyRegisters() {
        Patron patron = new Patron("P001", "Alice", "alice@test.com", "555-0001");
        patronService.addPatron(patron);

        assertTrue(patronService.findPatronById("P001").isPresent());
    }

    @Test
    void addPatron_duplicate_throwsException() {
        Patron patron = new Patron("P001", "Alice", "alice@test.com", "555-0001");
        patronService.addPatron(patron);

        assertThrows(IllegalArgumentException.class, () -> patronService.addPatron(patron));
    }

    @Test
    void updatePatron_updatesFields() {
        patronService.addPatron(new Patron("P001", "Alice", "old@test.com", "555-0001"));
        patronService.updatePatron("P001", "Alice Updated", "new@test.com", "555-9999");

        Patron updated = patronService.findPatronOrThrow("P001");
        assertEquals("Alice Updated", updated.getName());
        assertEquals("new@test.com", updated.getEmail());
    }

    @Test
    void findPatronOrThrow_notFound_throwsException() {
        assertThrows(PatronNotFoundException.class, () -> patronService.findPatronOrThrow("NONEXISTENT"));
    }

    @Test
    void removePatron_removesSuccessfully() {
        patronService.addPatron(new Patron("P001", "Alice", "alice@test.com", "555-0001"));
        patronService.removePatron("P001");

        assertTrue(patronService.findPatronById("P001").isEmpty());
    }

    @Test
    void book_withNullIsbn_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> new Patron(null, "Bob", "bob@test.com", "555-0002"));
    }
}
