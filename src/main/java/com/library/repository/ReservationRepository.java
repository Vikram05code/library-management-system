package com.library.repository;

import com.library.model.Reservation;

import java.util.*;

public interface ReservationRepository {
    void save(Reservation reservation);
    Optional<Reservation> findById(String reservationId);
    List<Reservation> findAll();
    List<Reservation> findActiveByIsbn(String isbn);
    List<Reservation> findByPatronId(String patronId);
    Optional<Reservation> findOldestActiveByIsbn(String isbn);
}
