package com.library.service;

import com.library.exception.BookNotFoundException;
import com.library.model.Book;
import com.library.pattern.strategy.SearchStrategy;
import com.library.pattern.strategy.SearchStrategyFactory;
import com.library.pattern.strategy.SearchType;
import com.library.repository.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class BookServiceImpl implements BookService{

    private static final Logger log = LoggerFactory.getLogger(BookServiceImpl.class);

    private final BookRepository bookRepository;
    private final BranchServiceImpl branchService;

    public BookServiceImpl(BookRepository bookRepository, BranchServiceImpl branchService) {
        this.bookRepository = bookRepository;
        this.branchService  = branchService;
    }

    @Override
    public void addBook(Book book, String branchId) {
        bookRepository.save(book);
        branchService.getBranch(branchId).addBook(book);
        log.info("Book added: isbn={} branch={}", book.getIsbn(), branchId);
    }

    @Override
    public void removeBook(String isbn) {
        Book book = findByIsbnOrThrow(isbn);
        bookRepository.delete(isbn);
        branchService.getAllBranches().forEach(branch -> branch.removeBook(book));
        log.info("Book removed: isbn={}", isbn);
    }

    @Override
    public void updateBook(String isbn, String newTitle, String newAuthor,
                           int newYear, Book.Genre newGenre) {
        Book book = findByIsbnOrThrow(isbn);
        book.setTitle(newTitle);
        book.setAuthor(newAuthor);
        book.setPublicationYear(newYear);
        book.setGenre(newGenre);
        bookRepository.save(book);
        log.info("Book updated: isbn={}", isbn);
    }

    @Override
    public List<Book> search(SearchType type, String query) {
        SearchStrategy strategy = SearchStrategyFactory.getInstance().get(type);
        List<Book> results = strategy.search(bookRepository.findAll(), query);
        log.debug("Search type={} query='{}' returned {} results", type, query, results.size());
        return results;
    }

    public List<Book> searchByTitle(String title)   { return search(SearchType.TITLE,  title);  }
    public List<Book> searchByAuthor(String author)  { return search(SearchType.AUTHOR, author); }
    public List<Book> searchByIsbn(String isbn)      { return search(SearchType.ISBN,   isbn);   }

    @Override
    public Optional<Book> findByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn);
    }

    @Override
    public Book findByIsbnOrThrow(String isbn) {
        return bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new BookNotFoundException("Book not found: isbn=" + isbn));
    }
    @Override
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }
}
