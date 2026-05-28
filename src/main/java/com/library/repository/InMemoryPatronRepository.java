package com.library.repository;

import com.library.model.Patron;

import java.util.*;

public class InMemoryPatronRepository implements PatronRepository {

    private final Map<String, Patron> store = new HashMap<>();

    @Override
    public void save(Patron patron) {
        store.put(patron.getPatronId(), patron);
    }

    @Override
    public Optional<Patron> findById(String patronId) {
        return Optional.ofNullable(store.get(patronId));
    }

    @Override
    public List<Patron> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void delete(String patronId) {
        store.remove(patronId);
    }

    @Override
    public boolean exists(String patronId) {
        return store.containsKey(patronId);
    }
}
