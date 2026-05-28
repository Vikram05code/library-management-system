package com.library.repository;

import com.library.model.Book;

import java.util.*;


public class InMemoryBookRepository implements BookRepository {

    private final Map<String, Book> store = new HashMap<>();

    @Override
    public void save(Book book) {
        store.put(book.getIsbn(), book);
    }

    @Override
    public Optional<Book> findByIsbn(String isbn) {
        return Optional.ofNullable(store.get(isbn));
    }

    @Override
    public List<Book> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void delete(String isbn) {
        store.remove(isbn);
    }

    @Override
    public boolean exists(String isbn) {
        return store.containsKey(isbn);
    }
}
