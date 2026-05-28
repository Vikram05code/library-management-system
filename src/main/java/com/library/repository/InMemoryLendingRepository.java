package com.library.repository;

import com.library.model.LendingRecord;

import java.util.*;

public class InMemoryLendingRepository implements LendingRepository {

    private final Map<String, LendingRecord> store = new LinkedHashMap<>();

    @Override
    public void save(LendingRecord record) {
        store.put(record.getRecordId(), record);
    }

    @Override
    public Optional<LendingRecord> findById(String recordId) {
        return Optional.ofNullable(store.get(recordId));
    }

    @Override
    public List<LendingRecord> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public List<LendingRecord> findByPatronId(String patronId) {
        return store.values().stream()
                .filter(r -> r.getPatronId().equals(patronId))
                .toList();
    }

    @Override
    public List<LendingRecord> findByIsbn(String isbn) {
        return store.values().stream()
                .filter(r -> r.getIsbn().equals(isbn))
                .toList();
    }

    @Override
    public List<LendingRecord> findActiveByIsbn(String isbn) {
        return store.values().stream()
                .filter(r -> r.getIsbn().equals(isbn) && !r.isReturned())
                .toList();
    }

    @Override
    public List<LendingRecord> findOverdue() {
        return store.values().stream()
                .filter(LendingRecord::isOverdue)
                .toList();
    }
}
