package com.library.service;

import com.library.model.Book;
import com.library.pattern.strategy.SearchType;

import java.util.List;
import java.util.Optional;

public interface BookService {
    public void addBook(Book book, String branchId);
    public void updateBook(String isbn, String newTitle, String newAuthor, int newYear, Book.Genre newGenre);
    public void removeBook(String bookId);
    public List<Book> search(SearchType type, String query);
    public Optional<Book> findByIsbn(String isbn);
    public Book findByIsbnOrThrow(String isbn);
    public List<Book> getAllBooks();
}
