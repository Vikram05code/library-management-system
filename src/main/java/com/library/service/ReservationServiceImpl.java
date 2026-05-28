package com.library.service;

import com.library.model.Patron;
import com.library.model.Reservation;
import com.library.pattern.observer.BookEvent;
import com.library.pattern.observer.EventPublisher;
import com.library.repository.ReservationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ReservationServiceImpl implements ReservationService {

    private static final Logger logger = LoggerFactory.getLogger(ReservationServiceImpl.class);

    private final ReservationRepository reservationRepository;
    private final PatronServiceImpl patronService;
    private final EventPublisher eventPublisher;

    public ReservationServiceImpl(ReservationRepository reservationRepository,
                                  PatronServiceImpl patronService,
                                  EventPublisher eventPublisher) {
        this.reservationRepository = reservationRepository;
        this.patronService         = patronService;
        this.eventPublisher        = eventPublisher;
    }

    @Override
    public Reservation placeReservation(String isbn, String patronId, String branchId) {
        Patron patron = patronService.findPatronOrThrow(patronId);

        // Prevent duplicate active reservations
        boolean alreadyReserved = reservationRepository.findByPatronId(patronId).stream()
                .anyMatch(r -> r.getIsbn().equals(isbn) && r.isActive());
        if (alreadyReserved) {
            throw new IllegalStateException("Patron " + patronId + " already has an active reservation for isbn=" + isbn);
        }

        Reservation reservation = new Reservation(patronId, isbn, branchId);
        reservationRepository.save(reservation);
        patron.addReservation(isbn);

        logger.info("Reservation placed: isbn={} patron={} branch={} expiry={}",
                isbn, patronId, branchId, reservation.getExpiryDate());
        return reservation;
    }

    @Override
    public void cancelReservation(String reservationId, String patronId) {
        reservationRepository.findById(reservationId).ifPresent(reservation -> {
            reservation.setStatus(Reservation.ReservationStatus.CANCELLED);
            reservationRepository.save(reservation);
            patronService.findPatronById(patronId)
                    .ifPresent(p -> p.removeReservation(reservation.getIsbn()));
            logger.info("Reservation cancelled: id={}", reservationId);
        });
    }

    @Override
    public void notifyNextReservation(String isbn, String branchId) {
        reservationRepository.findOldestActiveByIsbn(isbn).ifPresent(reservation -> {
            reservation.setStatus(Reservation.ReservationStatus.FULFILLED);
            reservationRepository.save(reservation);
            eventPublisher.publish(BookEvent.BOOK_AVAILABLE, isbn, reservation.getPatronId(), branchId);
            logger.info("Notified patron={} that isbn={} is available at branch={}",
                    reservation.getPatronId(), isbn, branchId);
        });
    }

    @Override
    public boolean hasActiveReservation(String isbn) {
        return !reservationRepository.findActiveByIsbn(isbn).isEmpty();
    }

    @Override
    public List<Reservation> getActiveReservationsForBook(String isbn) {
        return reservationRepository.findActiveByIsbn(isbn);
    }

    @Override
    public List<Reservation> getReservationsByPatron(String patronId) {
        return reservationRepository.findByPatronId(patronId);
    }

    @Override
    public void expireStaleReservations() {
        reservationRepository.findAll().stream()
                .filter(Reservation::isExpired)
                .forEach(reservation -> {
                    reservation.setStatus(Reservation.ReservationStatus.EXPIRED);
                    reservationRepository.save(reservation);
                    eventPublisher.publish(BookEvent.RESERVATION_EXPIRED,
                            reservation.getIsbn(), reservation.getPatronId(), reservation.getBranchId());
                    logger.info("Reservation expired: id={} isbn={} patron={}",
                            reservation.getReservationId(), reservation.getIsbn(), reservation.getPatronId());
                });
    }
}
