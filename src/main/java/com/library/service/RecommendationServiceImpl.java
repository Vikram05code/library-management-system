package com.library.service;

import com.library.model.Book;
import com.library.model.LendingRecord;
import com.library.model.Patron;
import com.library.repository.LendingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class RecommendationServiceImpl implements RecommendationService {

    private static final Logger logger = LoggerFactory.getLogger(RecommendationServiceImpl.class);
    private static final int DEFAULT_RECOMMENDATIONS = 5;

    private final PatronServiceImpl patronService;
    private final BookService bookService;
    private final LendingRepository lendingRepository;

    public RecommendationServiceImpl(PatronServiceImpl patronService,
                                     BookService bookService,
                                     LendingRepository lendingRepository) {
        this.patronService    = patronService;
        this.bookService      = bookService;
        this.lendingRepository = lendingRepository;
    }

    @Override
    public List<Book> recommend(String patronId, int limit) {
        Patron patron = patronService.findPatronOrThrow(patronId);
        List<LendingRecord> history = patron.getBorrowingHistory();

        if (history.isEmpty()) {
            logger.info("Patron {} has no borrowing history; returning popular books", patronId);
            return getMostPopularBooks(limit);
        }

        Set<String> alreadyRead = history.stream()
                .map(LendingRecord::getIsbn)
                .collect(Collectors.toSet());

        Map<Book.Genre, Long> genreCount = new HashMap<>();
        for (LendingRecord record : history) {
            bookService.findByIsbn(record.getIsbn()).ifPresent(book ->
                    genreCount.merge(book.getGenre(), 1L, Long::sum));
        }

        Map<Book, Double> scores = new HashMap<>();
        List<Book> candidates = bookService.getAllBooks().stream()
                .filter(b -> !alreadyRead.contains(b.getIsbn()))
                .filter(Book::isAvailable)
                .toList();

        Map<String, Long> globalBorrowCount = lendingRepository.findAll().stream()
                .collect(Collectors.groupingBy(LendingRecord::getIsbn, Collectors.counting()));

        for (Book book : candidates) {
            long genreScore     = genreCount.getOrDefault(book.getGenre(), 0L);
            long popularityScore = globalBorrowCount.getOrDefault(book.getIsbn(), 0L);
            // Weighted formula: genre match counts for 70%, popularity for 30%
            double score = (genreScore * 0.7) + (popularityScore * 0.3);
            scores.put(book, score);
        }

        List<Book> recommendations = scores.entrySet().stream()
                .sorted(Map.Entry.<Book, Double>comparingByValue().reversed())
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        logger.info("Generated {} recommendations for patron={}", recommendations.size(), patronId);
        return recommendations;
    }

    @Override
    public List<Book> recommend(String patronId) {
        return recommend(patronId, DEFAULT_RECOMMENDATIONS);
    }

    private List<Book> getMostPopularBooks(int limit) {
        Map<String, Long> counts = lendingRepository.findAll().stream()
                .collect(Collectors.groupingBy(LendingRecord::getIsbn, Collectors.counting()));

        return counts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(limit)
                .map(e -> bookService.findByIsbn(e.getKey()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }
}
