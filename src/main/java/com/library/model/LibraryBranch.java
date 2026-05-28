package com.library.model;

import java.util.*;


public class LibraryBranch {

    private final String branchId;
    private String name;
    private String address;
    // Map of ISBN -> list of Books (a branch can have multiple copies)
    private final Map<String, List<Book>> inventory;

    public LibraryBranch(String branchId, String name, String address) {
        if (branchId == null || branchId.isBlank()) throw new IllegalArgumentException("Branch ID cannot be blank.");
        this.branchId = branchId;
        this.name = name;
        this.address = address;
        this.inventory = new HashMap<>();
    }

    // --- Getters ---
    public String getBranchId() { return branchId; }
    public String getName() { return name; }
    public String getAddress() { return address; }

    // --- Setters ---
    public void setName(String name) { this.name = name; }
    public void setAddress(String address) { this.address = address; }

    /** Add a book copy to this branch's inventory. */
    public void addBook(Book book) {
        inventory.computeIfAbsent(book.getIsbn(), k -> new ArrayList<>()).add(book);
    }

    /** Remove a specific book copy from inventory. Returns true if removed. */
    public boolean removeBook(Book book) {
        List<Book> copies = inventory.get(book.getIsbn());
        if (copies == null) return false;
        boolean removed = copies.remove(book);
        if (copies.isEmpty()) inventory.remove(book.getIsbn());
        return removed;
    }

    /** Find a book by ISBN. Returns any copy (prefers available). */
    public Optional<Book> findBookByIsbn(String isbn) {
        List<Book> copies = inventory.getOrDefault(isbn, Collections.emptyList());
        return copies.stream()
                .filter(Book::isAvailable)
                .findFirst()
                .or(() -> copies.stream().findFirst());
    }

    /** Find all books by a given author (case-insensitive). */
    public List<Book> findBooksByAuthor(String author) {
        return inventory.values().stream()
                .flatMap(List::stream)
                .filter(b -> b.getAuthor().equalsIgnoreCase(author))
                .toList();
    }

    /** Find all books whose title contains the query string (case-insensitive). */
    public List<Book> findBooksByTitle(String titleQuery) {
        return inventory.values().stream()
                .flatMap(List::stream)
                .filter(b -> b.getTitle().toLowerCase().contains(titleQuery.toLowerCase()))
                .toList();
    }

    /** Returns all books in this branch's inventory. */
    public List<Book> getAllBooks() {
        return inventory.values().stream().flatMap(List::stream).toList();
    }

    /** Returns only available books. */
    public List<Book> getAvailableBooks() {
        return getAllBooks().stream().filter(Book::isAvailable).toList();
    }

    /** Returns only currently borrowed books. */
    public List<Book> getBorrowedBooks() {
        return getAllBooks().stream()
                .filter(b -> b.getStatus() == Book.BookStatus.BORROWED)
                .toList();
    }

    public boolean hasAvailableCopy(String isbn) {
        return inventory.getOrDefault(isbn, Collections.emptyList())
                .stream().anyMatch(Book::isAvailable);
    }

    @Override
    public String toString() {
        return String.format("LibraryBranch{id='%s', name='%s', address='%s', totalBooks=%d}",
                branchId, name, address, inventory.values().stream().mapToLong(List::size).sum());
    }
}
