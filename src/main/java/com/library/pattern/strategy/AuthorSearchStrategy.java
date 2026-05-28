package com.library.pattern.strategy;

import com.library.model.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public final class AuthorSearchStrategy extends AbstractSearchStrategy {

    private static final Logger log = LoggerFactory.getLogger(AuthorSearchStrategy.class);

    @Override
    protected List<Book> doSearch(List<Book> books, String query) {
        String lowerQuery = query.toLowerCase();
        List<Book> results = books.stream()
                .filter(book -> book.getAuthor().toLowerCase().contains(lowerQuery))
                .toList();

        log.debug("Author search: query='{}' matched {}/{} books", query, results.size(), books.size());
        return results;
    }
}
