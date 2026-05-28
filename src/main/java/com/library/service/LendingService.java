package com.library.service;

import com.library.model.LendingRecord;

import java.util.List;

public interface LendingService {
    public LendingRecord checkout(String isbn, String patronId, String branchId);
    public LendingRecord returnBook(String isbn, String patronId, String branchId);
    public List<LendingRecord> getOverdueRecords();
    public List<LendingRecord> getAllLendingRecords();
    public List<LendingRecord> getLendingHistoryByPatron(String patronId);
}
