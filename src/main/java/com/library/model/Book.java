package com.library.model;

import java.util.Objects;

public class Book {

    private final String isbn;
    private String title;
    private String author;
    private int publicationYear;
    private Genre genre;
    private BookStatus status;

    public enum BookStatus {
        AVAILABLE, BORROWED, RESERVED, TRANSFERRED
    }

    public enum Genre {
        FICTION, NON_FICTION, SCIENCE, HISTORY, BIOGRAPHY, TECHNOLOGY, PHILOSOPHY, ART, BUSINESS, SELF_HELP, OTHER
    }

    public Book(String isbn, String title, String author, int publicationYear, Genre genre) {
        if (isbn == null || isbn.isBlank()) throw new IllegalArgumentException("ISBN cannot be null or blank.");
        if (title == null || title.isBlank()) throw new IllegalArgumentException("Title cannot be null or blank.");
        if (author == null || author.isBlank()) throw new IllegalArgumentException("Author cannot be null or blank.");
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publicationYear = publicationYear;
        this.genre = genre;
        this.status = BookStatus.AVAILABLE;
    }

    // --- Getters ---
    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public int getPublicationYear() { return publicationYear; }
    public Genre getGenre() { return genre; }
    public BookStatus getStatus() { return status; }

    // --- Setters for mutable fields ---
    public void setTitle(String title) { this.title = title; }
    public void setAuthor(String author) { this.author = author; }
    public void setPublicationYear(int publicationYear) { this.publicationYear = publicationYear; }
    public void setGenre(Genre genre) { this.genre = genre; }
    public void setStatus(BookStatus status) { this.status = status; }

    public boolean isAvailable() { return this.status == BookStatus.AVAILABLE; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Book book)) return false;
        return Objects.equals(isbn, book.isbn);
    }

    @Override
    public int hashCode() { return Objects.hash(isbn); }

    @Override
    public String toString() {
        return String.format("Book{isbn='%s', title='%s', author='%s', year=%d, genre=%s, status=%s}",
                isbn, title, author, publicationYear, genre, status);
    }
}
