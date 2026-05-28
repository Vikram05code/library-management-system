package com.library.pattern.strategy;

import com.library.model.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public final class IsbnSearchStrategy extends AbstractSearchStrategy {

    private static final Logger log = LoggerFactory.getLogger(IsbnSearchStrategy.class);

    @Override
    protected List<Book> doSearch(List<Book> books, String query) {
        List<Book> results = books.stream()
                .filter(book -> book.getIsbn().equalsIgnoreCase(query))
                .toList();

        if (results.isEmpty()) {
            log.debug("ISBN search: no book found for isbn='{}'", query);
        } else {
            log.debug("ISBN search: found book for isbn='{}'", query);
        }
        return results;
    }
}
