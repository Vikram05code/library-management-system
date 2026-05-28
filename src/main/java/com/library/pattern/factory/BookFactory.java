package com.library.pattern.factory;

import com.library.model.Book;


public class BookFactory {

    private BookFactory() {}


    public static Book createBook(String isbn, String title, String author,
                                  int publicationYear, Book.Genre genre) {
        return new Book(isbn, title, author, publicationYear, genre);
    }


    public static Book createBook(String isbn, String title, String author, int publicationYear) {
        return new Book(isbn, title, author, publicationYear, Book.Genre.OTHER);
    }


    public static Book createCopy(Book original) {
        Book copy = new Book(
                original.getIsbn() + "-COPY-" + System.nanoTime(),
                original.getTitle(),
                original.getAuthor(),
                original.getPublicationYear(),
                original.getGenre()
        );
        copy.setStatus(Book.BookStatus.AVAILABLE);
        return copy;
    }


    public static Book createTransferCopy(Book original, String newIsbn) {
        Book transfer = new Book(newIsbn,
                original.getTitle(),
                original.getAuthor(),
                original.getPublicationYear(),
                original.getGenre());
        transfer.setStatus(Book.BookStatus.AVAILABLE);
        return transfer;
    }
}
