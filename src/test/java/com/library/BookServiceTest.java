package com.library;

import com.library.exception.BookNotFoundException;
import com.library.model.Book;
import com.library.model.LibraryBranch;
import com.library.pattern.factory.BookFactory;
import com.library.pattern.strategy.SearchType;
import com.library.repository.InMemoryBookRepository;
import com.library.service.BookServiceImpl;
import com.library.service.BranchServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookServiceTest {

    private BookServiceImpl bookService;
    private static final String BRANCH_ID = "B001";

    @BeforeEach
    void setUp() {
        BranchServiceImpl branchService = new BranchServiceImpl();
        branchService.addBranch(new LibraryBranch(BRANCH_ID, "Test Branch", "1 Test St"));
        bookService = new BookServiceImpl(new InMemoryBookRepository(), branchService);
    }

    @Test
    void addAndFindBook() {
        Book book = BookFactory.createBook("ISBN-001", "Dune", "Frank Herbert", 1965, Book.Genre.FICTION);
        bookService.addBook(book, BRANCH_ID);
        assertTrue(bookService.findByIsbn("ISBN-001").isPresent());
    }

    @Test
    void findByIsbnOrThrow_notFound_throwsException() {
        assertThrows(BookNotFoundException.class, () -> bookService.findByIsbnOrThrow("NONEXISTENT"));
    }

    @Test
    void removeBook_removesFromRegistry() {
        Book book = BookFactory.createBook("ISBN-002", "Clean Code", "Robert Martin", 2008, Book.Genre.TECHNOLOGY);
        bookService.addBook(book, BRANCH_ID);
        bookService.removeBook("ISBN-002");
        assertTrue(bookService.findByIsbn("ISBN-002").isEmpty());
    }

    @Test
    void updateBook_updatesFields() {
        Book book = BookFactory.createBook("ISBN-003", "Old Title", "Old Author", 2000, Book.Genre.OTHER);
        bookService.addBook(book, BRANCH_ID);
        bookService.updateBook("ISBN-003", "New Title", "New Author", 2024, Book.Genre.FICTION);
        Book updated = bookService.findByIsbnOrThrow("ISBN-003");
        assertEquals("New Title",      updated.getTitle());
        assertEquals("New Author",     updated.getAuthor());
        assertEquals(Book.Genre.FICTION, updated.getGenre());
    }

    @Test
    void searchByTitle_partialCaseInsensitive() {
        bookService.addBook(BookFactory.createBook("ISBN-004", "The Great Gatsby",  "Fitzgerald", 1925), BRANCH_ID);
        bookService.addBook(BookFactory.createBook("ISBN-005", "Great Expectations","Dickens",    1861), BRANCH_ID);
        bookService.addBook(BookFactory.createBook("ISBN-006", "Moby Dick",         "Melville",   1851), BRANCH_ID);

        List<Book> results = bookService.searchByTitle("great");
        assertEquals(2, results.size());
    }

    @Test
    void searchByAuthor_caseInsensitive() {
        bookService.addBook(BookFactory.createBook("ISBN-007", "Foundation", "Isaac Asimov", 1951, Book.Genre.FICTION), BRANCH_ID);
        List<Book> results = bookService.searchByAuthor("asimov");
        assertEquals(1, results.size());
        assertEquals("Foundation", results.get(0).getTitle());
    }

    @Test
    void searchByIsbn_exactMatch() {
        bookService.addBook(BookFactory.createBook("ISBN-008", "Dune", "Frank Herbert", 1965), BRANCH_ID);
        List<Book> found    = bookService.search(SearchType.ISBN, "ISBN-008");
        List<Book> notFound = bookService.search(SearchType.ISBN, "ISBN-999");
        assertEquals(1, found.size());
        assertTrue(notFound.isEmpty());
    }

    @Test
    void searchByGenre_returnsMatchingBooks() {
        bookService.addBook(BookFactory.createBook("ISBN-009", "Sapiens",    "Harari", 2011, Book.Genre.HISTORY),    BRANCH_ID);
        bookService.addBook(BookFactory.createBook("ISBN-010", "Clean Code", "Martin", 2008, Book.Genre.TECHNOLOGY), BRANCH_ID);

        List<Book> history = bookService.search(SearchType.GENRE, "HISTORY");
        assertEquals(1, history.size());
        assertEquals("ISBN-009", history.get(0).getIsbn());
    }

    @Test
    void search_nullQuery_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> bookService.search(SearchType.TITLE, null));
    }

    @Test
    void search_blankQuery_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> bookService.search(SearchType.TITLE, "   "));
    }
}
