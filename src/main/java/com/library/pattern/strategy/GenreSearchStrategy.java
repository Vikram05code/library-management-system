package com.library.pattern.strategy;

import com.library.model.Book;
import com.library.model.Book.Genre;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class GenreSearchStrategy extends AbstractSearchStrategy {

    private static final Logger log = LoggerFactory.getLogger(GenreSearchStrategy.class);

    private static final String VALID_GENRES = Arrays.stream(Genre.values())
            .map(Enum::name)
            .collect(Collectors.joining(", "));

    @Override
    protected List<Book> doSearch(List<Book> books, String query) {
        Optional<Genre> genre = parseGenre(query);

        if (genre.isEmpty()) {
            log.warn("Genre search: '{}' is not a recognised genre. Valid values: [{}]",
                    query, VALID_GENRES);
            return List.of();
        }

        List<Book> results = books.stream()
                .filter(book -> book.getGenre() == genre.get())
                .toList();

        log.debug("Genre search: query='{}' matched {}/{} books", query, results.size(), books.size());
        return results;
    }

    private Optional<Genre> parseGenre(String query) {
        try {
            return Optional.of(Genre.valueOf(query.toUpperCase()));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
