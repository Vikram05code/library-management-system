package com.library.model;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;


public class Reservation {

    public enum ReservationStatus { ACTIVE, FULFILLED, CANCELLED, EXPIRED }

    private final String reservationId;
    private final String patronId;
    private final String isbn;
    private final String branchId;
    private final LocalDate reservationDate;
    private final LocalDate expiryDate;
    private ReservationStatus status;

    public Reservation(String patronId, String isbn, String branchId) {
        this.reservationId = UUID.randomUUID().toString();
        this.patronId = patronId;
        this.isbn = isbn;
        this.branchId = branchId;
        this.reservationDate = LocalDate.now();
        this.expiryDate = reservationDate.plusDays(7); // reservation held for 7 days
        this.status = ReservationStatus.ACTIVE;
    }

    // --- Getters ---
    public String getReservationId() { return reservationId; }
    public String getPatronId() { return patronId; }
    public String getIsbn() { return isbn; }
    public String getBranchId() { return branchId; }
    public LocalDate getReservationDate() { return reservationDate; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public ReservationStatus getStatus() { return status; }

    public void setStatus(ReservationStatus status) { this.status = status; }

    public boolean isActive() { return status == ReservationStatus.ACTIVE; }
    public boolean isExpired() { return status == ReservationStatus.ACTIVE && LocalDate.now().isAfter(expiryDate); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Reservation that)) return false;
        return Objects.equals(reservationId, that.reservationId);
    }

    @Override
    public int hashCode() { return Objects.hash(reservationId); }

    @Override
    public String toString() {
        return String.format("Reservation{id='%s', patronId='%s', isbn='%s', branch='%s', status=%s, expiry=%s}",
                reservationId, patronId, isbn, branchId, status, expiryDate);
    }
}
