package com.library.repository;

import com.library.model.Reservation;

import java.util.*;

public class InMemoryReservationRepository implements ReservationRepository {

    private final Map<String, Reservation> store = new LinkedHashMap<>();

    @Override
    public void save(Reservation reservation) {
        store.put(reservation.getReservationId(), reservation);
    }

    @Override
    public Optional<Reservation> findById(String reservationId) {
        return Optional.ofNullable(store.get(reservationId));
    }

    @Override
    public List<Reservation> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public List<Reservation> findActiveByIsbn(String isbn) {
        return store.values().stream()
                .filter(r -> r.getIsbn().equals(isbn) && r.isActive())
                .toList();
    }

    @Override
    public List<Reservation> findByPatronId(String patronId) {
        return store.values().stream()
                .filter(r -> r.getPatronId().equals(patronId))
                .toList();
    }

    @Override
    public Optional<Reservation> findOldestActiveByIsbn(String isbn) {
        // LinkedHashMap preserves insertion order → first() is oldest
        return store.values().stream()
                .filter(r -> r.getIsbn().equals(isbn) && r.isActive())
                .findFirst();
    }
}
