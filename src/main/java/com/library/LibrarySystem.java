package com.library;

import com.library.model.Book;
import com.library.model.LendingRecord;
import com.library.model.LibraryBranch;
import com.library.model.Reservation;
import com.library.pattern.observer.EventPublisher;
import com.library.pattern.observer.NotificationService;
import com.library.repository.BookRepository;
import com.library.repository.InMemoryBookRepository;
import com.library.repository.InMemoryLendingRepository;
import com.library.repository.InMemoryPatronRepository;
import com.library.repository.InMemoryReservationRepository;
import com.library.repository.LendingRepository;
import com.library.repository.PatronRepository;
import com.library.repository.ReservationRepository;
import com.library.service.BookService;
import com.library.service.BookServiceImpl;
import com.library.service.BranchService;
import com.library.service.BranchServiceImpl;
import com.library.service.LendingService;
import com.library.service.LendingServiceImpl;
import com.library.service.PatronService;
import com.library.service.PatronServiceImpl;
import com.library.service.RecommendationService;
import com.library.service.RecommendationServiceImpl;
import com.library.service.ReservationService;
import com.library.service.ReservationServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public final class LibrarySystem {

    private static final Logger logger = LoggerFactory.getLogger(LibrarySystem.class);

    private final BookService bookService;
    private final PatronService patronService;
    private final BranchService branchService;
    private final LendingService lendingService;
    private final ReservationService reservationService;
    private final RecommendationService recommendationService;
    private final EventPublisher eventPublisher;

    public LibrarySystem() {
        // --- Repositories ---
        BookRepository bookRepo = new InMemoryBookRepository();
        PatronRepository patronRepo = new InMemoryPatronRepository();
        LendingRepository lendingRepo = new InMemoryLendingRepository();
        ReservationRepository reservationRepo = new InMemoryReservationRepository();

        this.eventPublisher = new EventPublisher();

        BranchServiceImpl branchServiceImpl = new BranchServiceImpl();
        branchServiceImpl.setEventPublisher(eventPublisher);
        this.branchService = branchServiceImpl;

        BookServiceImpl bookServiceImpl = new BookServiceImpl(bookRepo, branchServiceImpl);
        this.bookService = bookServiceImpl;

        PatronServiceImpl patronServiceImpl = new PatronServiceImpl(patronRepo);
        this.patronService = patronServiceImpl;

        ReservationServiceImpl reservationServiceImpl =
                new ReservationServiceImpl(reservationRepo, patronServiceImpl, eventPublisher);
        this.reservationService = reservationServiceImpl;

        LendingServiceImpl lendingServiceImpl = new LendingServiceImpl(
                lendingRepo, bookServiceImpl, patronServiceImpl,
                branchServiceImpl, eventPublisher, reservationServiceImpl);
        this.lendingService = lendingServiceImpl;

        RecommendationServiceImpl recommendationServiceImpl =
                new RecommendationServiceImpl(patronServiceImpl, bookServiceImpl, lendingRepo);
        this.recommendationService = recommendationServiceImpl;

        // --- Wire up notification observer ---
        NotificationService notificationService = new NotificationService(patronServiceImpl);
        eventPublisher.subscribe(notificationService);

        logger.info("Library Management System initialised successfully.");
    }

    public void addBranch(LibraryBranch branch) {
        branchService.addBranch(branch);
    }

    public void addBook(Book book, String branchId) {
        bookService.addBook(book, branchId);
    }

    public void registerPatron(com.library.model.Patron patron) {
        patronService.addPatron(patron);
    }

    public LendingRecord checkout(String isbn, String patronId, String branchId) {
        return lendingService.checkout(isbn, patronId, branchId);
    }

    public LendingRecord returnBook(String isbn, String patronId, String branchId) {
        return lendingService.returnBook(isbn, patronId, branchId);
    }

    public Reservation reserveBook(String isbn, String patronId, String branchId) {
        return reservationService.placeReservation(isbn, patronId, branchId);
    }

    public void transferBook(String isbn, String fromBranchId, String toBranchId) {
        branchService.transferBook(isbn, fromBranchId, toBranchId);
    }

    public List<Book> getRecommendations(String patronId) {
        return recommendationService.recommend(patronId);
    }

    public List<LendingRecord> getOverdueBooks() {
        return lendingService.getOverdueRecords();
    }

    public BookService getBookService() {
        return bookService;
    }

    public PatronService getPatronService() {
        return patronService;
    }

    public BranchService getBranchService() {
        return branchService;
    }

    public LendingService getLendingService() {
        return lendingService;
    }

    public ReservationService getReservationService() {
        return reservationService;
    }

    public RecommendationService getRecommendationService() {
        return recommendationService;
    }
}