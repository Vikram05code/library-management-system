package com.library.repository;

import com.library.model.LendingRecord;

import java.util.*;

public interface LendingRepository {
    void save(LendingRecord record);
    Optional<LendingRecord> findById(String recordId);
    List<LendingRecord> findAll();
    List<LendingRecord> findByPatronId(String patronId);
    List<LendingRecord> findByIsbn(String isbn);
    List<LendingRecord> findActiveByIsbn(String isbn);   // not yet returned
    List<LendingRecord> findOverdue();
}
