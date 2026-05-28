package com.library.service;

import com.library.exception.BranchNotFoundException;
import com.library.model.Book;
import com.library.model.LibraryBranch;
import com.library.pattern.observer.BookEvent;
import com.library.pattern.observer.EventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class BranchServiceImpl implements BranchService{

    private static final Logger logger = LoggerFactory.getLogger(BranchServiceImpl.class);

    private final Map<String, LibraryBranch> branches = new HashMap<>();
    private EventPublisher eventPublisher;

    @Override
    public void setEventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void addBranch(LibraryBranch branch) {
        branches.put(branch.getBranchId(), branch);
        logger.info("Branch added: id={} name={}", branch.getBranchId(), branch.getName());
    }

    @Override
    public LibraryBranch getBranch(String branchId) {
        LibraryBranch branch = branches.get(branchId);
        if (branch == null) throw new BranchNotFoundException("Branch not found: id=" + branchId);
        return branch;
    }

    @Override
    public Collection<LibraryBranch> getAllBranches() {
        return Collections.unmodifiableCollection(branches.values());
    }

    @Override
    public void transferBook(String isbn, String fromBranchId, String toBranchId) {
        LibraryBranch from = getBranch(fromBranchId);
        LibraryBranch to   = getBranch(toBranchId);

        Book book = from.findBookByIsbn(isbn)
                .filter(Book::isAvailable)
                .orElseThrow(() -> new IllegalStateException(
                        "No available copy of isbn=" + isbn + " at branch=" + fromBranchId));

        book.setStatus(Book.BookStatus.TRANSFERRED);
        from.removeBook(book);
        book.setStatus(Book.BookStatus.AVAILABLE);
        to.addBook(book);

        logger.info("Book transferred: isbn={} from={} to={}", isbn, fromBranchId, toBranchId);

        if (eventPublisher != null) {
            eventPublisher.publish(BookEvent.BOOK_TRANSFERRED, isbn, null, toBranchId);
        }
    }

    @Override
    public List<Book> getAvailableBooksByBranch(String branchId) {
        return getBranch(branchId).getAvailableBooks();
    }

    @Override
    public List<Book> getBorrowedBooksByBranch(String branchId) {
        return getBranch(branchId).getBorrowedBooks();
    }
}
