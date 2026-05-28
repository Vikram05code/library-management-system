package com.library.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


public class Patron {

    private final String patronId;
    private String name;
    private String email;
    private String phoneNumber;
    private final List<LendingRecord> borrowingHistory;
    private final List<String> reservedIsbns;

    public Patron(String patronId, String name, String email, String phoneNumber) {
        if (patronId == null || patronId.isBlank()) throw new IllegalArgumentException("Patron ID cannot be null or blank.");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Name cannot be null or blank.");
        this.patronId = patronId;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.borrowingHistory = new ArrayList<>();
        this.reservedIsbns = new ArrayList<>();
    }

    // --- Getters ---
    public String getPatronId() { return patronId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }

    public List<LendingRecord> getBorrowingHistory() {
        return Collections.unmodifiableList(borrowingHistory);
    }

    public List<String> getReservedIsbns() {
        return Collections.unmodifiableList(reservedIsbns);
    }

    // --- Setters ---
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    // --- Borrowing history management ---
    public void addLendingRecord(LendingRecord record) {
        borrowingHistory.add(record);
    }

    public void addReservation(String isbn) {
        if (!reservedIsbns.contains(isbn)) reservedIsbns.add(isbn);
    }

    public void removeReservation(String isbn) {
        reservedIsbns.remove(isbn);
    }

    public boolean hasActiveReservation(String isbn) {
        return reservedIsbns.contains(isbn);
    }

    /** Returns only the currently borrowed (not yet returned) books. */
    public List<LendingRecord> getCurrentlyBorrowedBooks() {
        return borrowingHistory.stream()
                .filter(r -> r.getReturnDate() == null)
                .toList();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Patron patron)) return false;
        return Objects.equals(patronId, patron.patronId);
    }

    @Override
    public int hashCode() { return Objects.hash(patronId); }

    @Override
    public String toString() {
        return String.format("Patron{id='%s', name='%s', email='%s', phone='%s'}",
                patronId, name, email, phoneNumber);
    }
}
