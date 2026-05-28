package com.library.pattern.strategy;

import com.library.model.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public final class TitleSearchStrategy extends AbstractSearchStrategy {

    private static final Logger log = LoggerFactory.getLogger(TitleSearchStrategy.class);

    @Override
    protected List<Book> doSearch(List<Book> books, String query) {
        String lowerQuery = query.toLowerCase();
        List<Book> results = books.stream()
                .filter(book -> book.getTitle().toLowerCase().contains(lowerQuery))
                .toList();

        log.debug("Title search: query='{}' matched {}/{} books", query, results.size(), books.size());
        return results;
    }
}
