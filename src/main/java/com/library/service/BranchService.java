package com.library.service;

import com.library.model.Book;
import com.library.model.LibraryBranch;
import com.library.pattern.observer.EventPublisher;

import java.util.Collection;
import java.util.List;

public interface BranchService {

    public void setEventPublisher(EventPublisher eventPublisher);
    public void addBranch(LibraryBranch branch);
    public LibraryBranch getBranch(String branchId);
    public Collection<LibraryBranch> getAllBranches();
    public void transferBook(String isbn, String fromBranchId, String toBranchId);
    public List<Book> getAvailableBooksByBranch(String branchId);
    public List<Book> getBorrowedBooksByBranch(String branchId);
}
