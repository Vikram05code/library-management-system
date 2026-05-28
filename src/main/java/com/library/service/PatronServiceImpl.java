package com.library.service;

import com.library.exception.PatronNotFoundException;
import com.library.model.LendingRecord;
import com.library.model.Patron;
import com.library.repository.PatronRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class PatronServiceImpl implements PatronService {

    private static final Logger logger = LoggerFactory.getLogger(PatronServiceImpl.class);

    private final PatronRepository patronRepository;

    public PatronServiceImpl(PatronRepository patronRepository) {
        this.patronRepository = patronRepository;
    }

    @Override
    public void addPatron(Patron patron) {
        if (patronRepository.exists(patron.getPatronId())) {
            throw new IllegalArgumentException("Patron already exists: id=" + patron.getPatronId());
        }
        patronRepository.save(patron);
        logger.info("Patron registered: id={} name={}", patron.getPatronId(), patron.getName());
    }

    @Override
    public void updatePatron(String patronId, String newName, String newEmail, String newPhone) {
        Patron patron = findPatronOrThrow(patronId);
        patron.setName(newName);
        patron.setEmail(newEmail);
        patron.setPhoneNumber(newPhone);
        patronRepository.save(patron);
        logger.info("Patron updated: id={}", patronId);
    }

    @Override
    public void removePatron(String patronId) {
        findPatronOrThrow(patronId); // validate exists
        patronRepository.delete(patronId);
        logger.info("Patron removed: id={}", patronId);
    }

    @Override
    public Optional<Patron> findPatronById(String patronId) {
        return patronRepository.findById(patronId);
    }

    public Patron findPatronOrThrow(String patronId) {
        return patronRepository.findById(patronId)
                .orElseThrow(() -> new PatronNotFoundException("Patron not found: id=" + patronId));
    }

    @Override
    public List<Patron> getAllPatrons() {
        return patronRepository.findAll();
    }

    @Override
    public List<LendingRecord> getBorrowingHistory(String patronId) {
        Patron patron = findPatronOrThrow(patronId);
        return patron.getBorrowingHistory();
    }

    @Override
    public List<LendingRecord> getCurrentLoans(String patronId) {
        Patron patron = findPatronOrThrow(patronId);
        return patron.getCurrentlyBorrowedBooks();
    }

    @Override
    public void addLendingRecord(String patronId, LendingRecord record) {
        Patron patron = findPatronOrThrow(patronId);
        patron.addLendingRecord(record);
        patronRepository.save(patron);
    }
}
