package com.library.pattern.strategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

public final class SearchStrategyFactory {

    private static final Logger log = LoggerFactory.getLogger(SearchStrategyFactory.class);

    private static final SearchStrategyFactory INSTANCE = new SearchStrategyFactory();

    private final Map<SearchType, SearchStrategy> registry;

    private SearchStrategyFactory() {
        Map<SearchType, SearchStrategy> map = new EnumMap<>(SearchType.class);
        map.put(SearchType.TITLE,  new TitleSearchStrategy());
        map.put(SearchType.AUTHOR, new AuthorSearchStrategy());
        map.put(SearchType.ISBN,   new IsbnSearchStrategy());
        map.put(SearchType.GENRE,  new GenreSearchStrategy());
        registry = Collections.synchronizedMap(map);
        log.debug("SearchStrategyFactory initialised with {} strategies", registry.size());
    }

    public static SearchStrategyFactory getInstance() {
        return INSTANCE;
    }

    public SearchStrategy get(SearchType type) {
        return Optional.ofNullable(registry.get(type))
                .orElseThrow(() -> new IllegalArgumentException(
                        "No SearchStrategy registered for type: " + type));
    }

    public void register(SearchType type, SearchStrategy strategy) {
        if (strategy == null) {
            throw new IllegalArgumentException("strategy must not be null");
        }
        registry.put(type, strategy);
        log.info("SearchStrategy registered: type={} impl={}", type, strategy.getClass().getSimpleName());
    }

    public Map<SearchType, SearchStrategy> getAll() {
        return Collections.unmodifiableMap(new EnumMap<>(registry));
    }
}
