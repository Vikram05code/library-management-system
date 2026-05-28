package com.library.repository;

import com.library.model.Patron;

import java.util.*;

public interface PatronRepository {
    void save(Patron patron);
    Optional<Patron> findById(String patronId);
    List<Patron> findAll();
    void delete(String patronId);
    boolean exists(String patronId);
}
