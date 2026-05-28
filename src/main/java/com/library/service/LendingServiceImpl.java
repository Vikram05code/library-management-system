package com.library.service;

import com.library.exception.BookNotAvailableException;
import com.library.model.Book;
import com.library.model.LendingRecord;
import com.library.model.LibraryBranch;
import com.library.model.Patron;
import com.library.pattern.observer.BookEvent;
import com.library.pattern.observer.EventPublisher;
import com.library.repository.LendingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;

public class LendingServiceImpl implements LendingService {

    private static final Logger logger = LoggerFactory.getLogger(LendingServiceImpl.class);

    private final LendingRepository lendingRepository;
    private final BookService bookService;
    private final PatronServiceImpl patronService;
    private final BranchServiceImpl branchService;
    private final EventPublisher eventPublisher;
    private final ReservationServiceImpl reservationService;

    public LendingServiceImpl(LendingRepository lendingRepository,
                              BookService bookService,
                              PatronServiceImpl patronService,
                              BranchServiceImpl branchService,
                              EventPublisher eventPublisher,
                              ReservationServiceImpl reservationService) {
        this.lendingRepository   = lendingRepository;
        this.bookService         = bookService;
        this.patronService       = patronService;
        this.branchService       = branchService;
        this.eventPublisher      = eventPublisher;
        this.reservationService  = reservationService;
    }

    @Override
    public LendingRecord checkout(String isbn, String patronId, String branchId) {
        Patron patron = patronService.findPatronOrThrow(patronId);
        bookService.findByIsbnOrThrow(isbn);  // validate book exists globally
        LibraryBranch branch = branchService.getBranch(branchId);

        Book book = branch.findBookByIsbn(isbn)
                .filter(Book::isAvailable)
                .orElseThrow(() -> new BookNotAvailableException(
                        "No available copy of isbn=" + isbn + " at branch=" + branchId));

        // Mark as BORROWED
        book.setStatus(Book.BookStatus.BORROWED);

        // Create and persist lending record
        LendingRecord record = new LendingRecord(patronId, isbn, branchId);
        lendingRepository.save(record);

        // Attach to patron's history
        patronService.addLendingRecord(patronId, record);

        logger.info("Checkout: isbn={} patron={} branch={} due={}",
                isbn, patronId, branchId, record.getDueDate());
        return record;
    }

    @Override
    public LendingRecord returnBook(String isbn, String patronId, String branchId) {
        // Find the active lending record for this patron & isbn
        LendingRecord record = lendingRepository.findByPatronId(patronId).stream()
                .filter(r -> r.getIsbn().equals(isbn) && !r.isReturned())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "No active loan found for patron=" + patronId + " isbn=" + isbn));

        record.setReturnDate(LocalDate.now());
        lendingRepository.save(record);

        // Update book status based on whether there are pending reservations
        LibraryBranch branch = branchService.getBranch(branchId);
        Book book = branch.findBookByIsbn(isbn)
                .orElseGet(() -> bookService.findByIsbnOrThrow(isbn));

        // Check for reservations
        boolean hasReservation = reservationService.hasActiveReservation(isbn);
        if (hasReservation) {
            book.setStatus(Book.BookStatus.RESERVED);
            // Notify the next patron in the reservation queue
            reservationService.notifyNextReservation(isbn, branchId);
        } else {
            book.setStatus(Book.BookStatus.AVAILABLE);
        }

        eventPublisher.publish(BookEvent.BOOK_RETURNED, isbn, patronId, branchId);
        logger.info("Return: isbn={} patron={} branch={} returned={} overdue={}",
                isbn, patronId, branchId, record.getReturnDate(), record.isOverdue());

        return record;
    }

    @Override
    public List<LendingRecord> getOverdueRecords() {
        return lendingRepository.findOverdue();
    }

    @Override
    public List<LendingRecord> getAllLendingRecords() {
        return lendingRepository.findAll();
    }

    @Override
    public List<LendingRecord> getLendingHistoryByPatron(String patronId) {
        return lendingRepository.findByPatronId(patronId);
    }
}
