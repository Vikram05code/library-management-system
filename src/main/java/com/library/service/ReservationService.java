package com.library.service;

import com.library.model.Reservation;

import java.util.List;

public interface ReservationService {

    public Reservation placeReservation(String isbn, String patronId, String branchId);
    public void cancelReservation(String reservationId, String patronId);
    public void notifyNextReservation(String isbn, String branchId);
    public boolean hasActiveReservation(String isbn);
    public List<Reservation> getActiveReservationsForBook(String isbn);
    public List<Reservation> getReservationsByPatron(String patronId);
    public void expireStaleReservations();
}
