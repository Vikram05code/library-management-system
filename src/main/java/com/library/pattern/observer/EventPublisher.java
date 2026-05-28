package com.library.pattern.observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


public class EventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(EventPublisher.class);
    private final List<LibraryObserver> observers = new ArrayList<>();

    public void subscribe(LibraryObserver observer) {
        observers.add(observer);
        logger.debug("Observer registered: {}", observer.getClass().getSimpleName());
    }

    public void unsubscribe(LibraryObserver observer) {
        observers.remove(observer);
    }


    public void publish(BookEvent event, String isbn, String patronId, String branchId) {
        logger.info("[EVENT] Publishing event={} isbn={} patron={} branch={}", event, isbn, patronId, branchId);
        for (LibraryObserver observer : observers) {
            try {
                observer.onEvent(event, isbn, patronId, branchId);
            } catch (Exception e) {
                logger.error("Observer {} threw an exception handling event {}: {}",
                        observer.getClass().getSimpleName(), event, e.getMessage(), e);
            }
        }
    }
}
