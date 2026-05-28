package com.library.pattern.strategy;

import com.library.model.Book;
import com.library.pattern.factory.BookFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for every concrete {@link SearchStrategy} implementation
 * and for {@link SearchStrategyFactory}.
 *
 * Each strategy is isolated in its own @Nested class so failures are easy to locate.
 */
class SearchStrategyTest {

    private List<Book> books;

    @BeforeEach
    void setUp() {
        books = List.of(
            BookFactory.createBook("ISBN-001", "Dune",                    "Frank Herbert",     1965, Book.Genre.FICTION),
            BookFactory.createBook("ISBN-002", "Dune Messiah",            "Frank Herbert",     1969, Book.Genre.FICTION),
            BookFactory.createBook("ISBN-003", "Clean Code",              "Robert C. Martin",  2008, Book.Genre.TECHNOLOGY),
            BookFactory.createBook("ISBN-004", "The Pragmatic Programmer","Andrew Hunt",       1999, Book.Genre.TECHNOLOGY),
            BookFactory.createBook("ISBN-005", "Sapiens",                 "Yuval Noah Harari", 2011, Book.Genre.HISTORY)
        );
    }

    // ── Shared validation (AbstractSearchStrategy) ────────────────────────────

    @Nested
    class ValidationTests {

        private final SearchStrategy strategy = new TitleSearchStrategy();

        @Test
        void nullBooksList_throwsNPE() {
            assertThrows(NullPointerException.class, () -> strategy.search(null, "Dune"));
        }

        @Test
        void nullQuery_throwsNPE() {
            assertThrows(NullPointerException.class, () -> strategy.search(books, null));
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " ", "   ", "\t"})
        void blankQuery_throwsIllegalArgument(String blank) {
            assertThrows(IllegalArgumentException.class, () -> strategy.search(books, blank));
        }

        @Test
        void queryIsTrimmedBeforeMatching() {
            List<Book> withPadding    = strategy.search(books, "  Dune  ");
            List<Book> withoutPadding = strategy.search(books, "Dune");
            assertEquals(withoutPadding.size(), withPadding.size());
        }

        @Test
        void emptyBooksList_returnsEmptyResult() {
            assertTrue(strategy.search(List.of(), "Dune").isEmpty());
        }
    }

    // ── TitleSearchStrategy ───────────────────────────────────────────────────

    @Nested
    class TitleSearchStrategyTests {

        private final SearchStrategy strategy = new TitleSearchStrategy();

        @Test
        void partialMatch_returnsAllMatchingBooks() {
            assertEquals(2, strategy.search(books, "Dune").size());
        }

        @Test
        void caseInsensitiveMatch() {
            assertEquals(2, strategy.search(books, "dune").size());
        }

        @Test
        void noMatch_returnsEmptyList() {
            assertTrue(strategy.search(books, "Invisible Man").isEmpty());
        }

        @Test
        void resultList_isImmutable() {
            List<Book> result = strategy.search(books, "Dune");
            assertThrows(UnsupportedOperationException.class, () -> result.add(books.get(0)));
        }
    }

    // ── AuthorSearchStrategy ──────────────────────────────────────────────────

    @Nested
    class AuthorSearchStrategyTests {

        private final SearchStrategy strategy = new AuthorSearchStrategy();

        @Test
        void lastNameOnly_matchesMultipleBooks() {
            assertEquals(2, strategy.search(books, "Herbert").size());
        }

        @Test
        void caseInsensitiveFullName() {
            assertEquals(2, strategy.search(books, "frank herbert").size());
        }

        @Test
        void noMatch_returnsEmptyList() {
            assertTrue(strategy.search(books, "Tolkien").isEmpty());
        }

        @Test
        void partialFirstName_matchesSingleBook() {
            List<Book> result = strategy.search(books, "Robert");
            assertEquals(1, result.size());
            assertEquals("ISBN-003", result.get(0).getIsbn());
        }
    }

    // ── IsbnSearchStrategy ────────────────────────────────────────────────────

    @Nested
    class IsbnSearchStrategyTests {

        private final SearchStrategy strategy = new IsbnSearchStrategy();

        @Test
        void exactIsbn_returnsOneBook() {
            List<Book> result = strategy.search(books, "ISBN-001");
            assertEquals(1, result.size());
            assertEquals("Dune", result.get(0).getTitle());
        }

        @Test
        void caseInsensitiveIsbn_matches() {
            assertEquals(1, strategy.search(books, "isbn-001").size());
        }

        @Test
        void unknownIsbn_returnsEmptyList() {
            assertTrue(strategy.search(books, "ISBN-999").isEmpty());
        }

        @Test
        void partialIsbn_doesNotMatch() {
            assertTrue(strategy.search(books, "ISBN").isEmpty());
        }
    }

    // ── GenreSearchStrategy ───────────────────────────────────────────────────

    @Nested
    class GenreSearchStrategyTests {

        private final SearchStrategy strategy = new GenreSearchStrategy();

        @Test
        void validGenre_returnsMatchingBooks() {
            assertEquals(2, strategy.search(books, "FICTION").size());
        }

        @Test
        void caseInsensitiveGenre() {
            assertEquals(
                strategy.search(books, "FICTION").size(),
                strategy.search(books, "fiction").size()
            );
        }

        @Test
        void unknownGenre_returnsEmptyList_doesNotThrow() {
            List<Book> result = assertDoesNotThrow(() -> strategy.search(books, "MANGA"));
            assertTrue(result.isEmpty());
        }

        @Test
        void technologyGenre_returnsCorrectCount() {
            assertEquals(2, strategy.search(books, "TECHNOLOGY").size());
        }
    }

    // ── SearchStrategyFactory ─────────────────────────────────────────────────

    @Nested
    class SearchStrategyFactoryTests {

        private final SearchStrategyFactory factory = SearchStrategyFactory.getInstance();

        @Test
        void getInstance_alwaysReturnsSameObject() {
            assertSame(SearchStrategyFactory.getInstance(), SearchStrategyFactory.getInstance());
        }

        @Test
        void get_returnsCorrectTypeForEachSearchType() {
            assertInstanceOf(TitleSearchStrategy.class,  factory.get(SearchType.TITLE));
            assertInstanceOf(AuthorSearchStrategy.class, factory.get(SearchType.AUTHOR));
            assertInstanceOf(IsbnSearchStrategy.class,   factory.get(SearchType.ISBN));
            assertInstanceOf(GenreSearchStrategy.class,  factory.get(SearchType.GENRE));
        }

        @Test
        void get_returnsCachedInstance() {
            assertSame(factory.get(SearchType.TITLE), factory.get(SearchType.TITLE));
        }

        @Test
        void register_replacesExistingStrategy() {
            SearchStrategy custom = (bookList, query) -> List.of();
            factory.register(SearchType.TITLE, custom);
            assertSame(custom, factory.get(SearchType.TITLE));
            // Restore
            factory.register(SearchType.TITLE, new TitleSearchStrategy());
        }

        @Test
        void register_nullStrategy_throwsIllegalArgument() {
            assertThrows(IllegalArgumentException.class,
                    () -> factory.register(SearchType.TITLE, null));
        }

        @Test
        void getAll_containsAllFourTypes() {
            assertEquals(4, factory.getAll().size());
            assertTrue(factory.getAll().containsKey(SearchType.TITLE));
            assertTrue(factory.getAll().containsKey(SearchType.AUTHOR));
            assertTrue(factory.getAll().containsKey(SearchType.ISBN));
            assertTrue(factory.getAll().containsKey(SearchType.GENRE));
        }

        @Test
        void getAll_returnsUnmodifiableMap() {
            assertThrows(UnsupportedOperationException.class,
                    () -> factory.getAll().put(SearchType.TITLE, (bl, q) -> List.of()));
        }
    }
}
