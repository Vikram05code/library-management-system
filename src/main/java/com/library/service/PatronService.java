package com.library.service;

import com.library.model.LendingRecord;
import com.library.model.Patron;

import java.util.List;
import java.util.Optional;

public interface PatronService {

    public void addPatron(Patron patron);
    public void updatePatron(String patronId, String newName, String newEmail, String newPhone);
    public void removePatron(String patronId);
    public Optional<Patron> findPatronById(String patronId);
    public List<Patron> getAllPatrons();
    public List<LendingRecord> getBorrowingHistory(String patronId);
    public List<LendingRecord> getCurrentLoans(String patronId);
    public void addLendingRecord(String patronId, LendingRecord record);
}
