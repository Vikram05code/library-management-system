package com.library.repository;

import com.library.model.Book;

import java.util.List;
import java.util.Optional;

public interface BookRepository {
    void save(Book book);
    Optional<Book> findByIsbn(String isbn);
    List<Book> findAll();
    void delete(String isbn);
    boolean exists(String isbn);
}
