package com.library.model;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;


public class LendingRecord {

    private final String recordId;
    private final String patronId;
    private final String isbn;
    private final String branchId;
    private final LocalDate checkoutDate;
    private LocalDate dueDate;
    private LocalDate returnDate;

    public LendingRecord(String patronId, String isbn, String branchId) {
        this.recordId = UUID.randomUUID().toString();
        this.patronId = patronId;
        this.isbn = isbn;
        this.branchId = branchId;
        this.checkoutDate = LocalDate.now();
        this.dueDate = checkoutDate.plusWeeks(2); // default 2-week loan period
    }

    // --- Getters ---
    public String getRecordId() { return recordId; }
    public String getPatronId() { return patronId; }
    public String getIsbn() { return isbn; }
    public String getBranchId() { return branchId; }
    public LocalDate getCheckoutDate() { return checkoutDate; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getReturnDate() { return returnDate; }

    public boolean isReturned() { return returnDate != null; }
    public boolean isOverdue() { return returnDate == null && LocalDate.now().isAfter(dueDate); }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LendingRecord that)) return false;
        return Objects.equals(recordId, that.recordId);
    }

    @Override
    public int hashCode() { return Objects.hash(recordId); }

    @Override
    public String toString() {
        return String.format("LendingRecord{id='%s', patronId='%s', isbn='%s', branch='%s', checkout=%s, due=%s, returned=%s}",
                recordId, patronId, isbn, branchId, checkoutDate, dueDate, returnDate != null ? returnDate : "NOT RETURNED");
    }
}
