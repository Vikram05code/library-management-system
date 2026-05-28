package com.library;

import com.library.exception.BranchNotFoundException;
import com.library.model.Book;
import com.library.model.LibraryBranch;
import com.library.pattern.factory.BookFactory;
import com.library.repository.InMemoryBookRepository;
import com.library.service.BookServiceImpl;
import com.library.service.BranchServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BranchServiceTest {

    private BranchServiceImpl branchService;
    private BookServiceImpl bookService;

    @BeforeEach
    void setUp() {
        branchService = new BranchServiceImpl();
        branchService.addBranch(new LibraryBranch("B001", "Central", "1 Main St"));
        branchService.addBranch(new LibraryBranch("B002", "East",    "2 East St"));
        bookService = new BookServiceImpl(new InMemoryBookRepository(), branchService);
    }

    @Test
    void addBranch_andRetrieve() {
        LibraryBranch branch = branchService.getBranch("B001");
        assertEquals("Central", branch.getName());
    }

    @Test
    void getBranch_notFound_throwsException() {
        assertThrows(BranchNotFoundException.class, () -> branchService.getBranch("NONEXISTENT"));
    }

    @Test
    void transferBook_movesBookBetweenBranches() {
        Book book = BookFactory.createBook("ISBN-001", "Dune", "Frank Herbert", 1965, Book.Genre.FICTION);
        bookService.addBook(book, "B001");

        assertTrue(branchService.getBranch("B001").hasAvailableCopy("ISBN-001"));
        assertFalse(branchService.getBranch("B002").hasAvailableCopy("ISBN-001"));

        branchService.transferBook("ISBN-001", "B001", "B002");

        assertFalse(branchService.getBranch("B001").hasAvailableCopy("ISBN-001"));
        assertTrue(branchService.getBranch("B002").hasAvailableCopy("ISBN-001"));
    }

    @Test
    void transferBook_whenNotAvailable_throwsException() {
        Book book = BookFactory.createBook("ISBN-002", "Clean Code", "Robert Martin", 2008);
        bookService.addBook(book, "B001");
        book.setStatus(Book.BookStatus.BORROWED);

        assertThrows(IllegalStateException.class,
                () -> branchService.transferBook("ISBN-002", "B001", "B002"));
    }

    @Test
    void getAllBranches_returnsAll() {
        assertEquals(2, branchService.getAllBranches().size());
    }
}
