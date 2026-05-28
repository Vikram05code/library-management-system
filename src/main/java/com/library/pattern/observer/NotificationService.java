package com.library.pattern.observer;

import com.library.service.PatronServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class NotificationService implements LibraryObserver {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final PatronServiceImpl patronService;

    public NotificationService(PatronServiceImpl patronService) {
        this.patronService = patronService;
    }

    @Override
    public void onEvent(BookEvent event, String isbn, String patronId, String branchId) {
        switch (event) {
            case BOOK_AVAILABLE -> notifyBookAvailable(isbn, patronId, branchId);
            case RESERVATION_EXPIRED -> notifyReservationExpired(isbn, patronId, branchId);
            case BOOK_OVERDUE -> notifyOverdue(isbn, patronId, branchId);
            case BOOK_TRANSFERRED -> logger.info("[NOTIFICATION] Book ISBN={} transferred to branch={}", isbn, branchId);
            default -> logger.debug("[NOTIFICATION] Unhandled event {} for isbn={}", event, isbn);
        }
    }

    private void notifyBookAvailable(String isbn, String patronId, String branchId) {
        patronService.findPatronById(patronId).ifPresentOrElse(
            patron -> logger.info(
                "[NOTIFICATION] Dear {}, the book ISBN={} you reserved is now available at branch={}. "
                + "You have 7 days to pick it up. — Library System",
                patron.getName(), isbn, branchId),
            () -> logger.warn("[NOTIFICATION] Could not find patron {} to notify for isbn={}", patronId, isbn)
        );
    }

    private void notifyReservationExpired(String isbn, String patronId, String branchId) {
        patronService.findPatronById(patronId).ifPresent(patron ->
            logger.info("[NOTIFICATION] Dear {}, your reservation for ISBN={} at branch={} has expired.",
                patron.getName(), isbn, branchId)
        );
    }

    private void notifyOverdue(String isbn, String patronId, String branchId) {
        patronService.findPatronById(patronId).ifPresent(patron ->
            logger.warn("[NOTIFICATION] Dear {}, the book ISBN={} borrowed from branch={} is OVERDUE. "
                + "Please return it as soon as possible.",
                patron.getName(), isbn, branchId)
        );
    }
}
