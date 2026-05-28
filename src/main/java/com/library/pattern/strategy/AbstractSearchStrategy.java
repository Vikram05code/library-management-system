package com.library.pattern.strategy;

import com.library.model.Book;

import java.util.List;
import java.util.Objects;

abstract class AbstractSearchStrategy implements SearchStrategy {

    @Override
    public final List<Book> search(List<Book> books, String query) {
        Objects.requireNonNull(books, "books list must not be null");
        Objects.requireNonNull(query, "search query must not be null");
        if (query.isBlank()) {
            throw new IllegalArgumentException("search query must not be blank");
        }
        return doSearch(books, query.trim());
    }

    protected abstract List<Book> doSearch(List<Book> books, String query);
}
