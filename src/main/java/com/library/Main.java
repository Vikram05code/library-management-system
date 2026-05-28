
package com.library;

import com.library.model.*;
import com.library.pattern.factory.BookFactory;
import com.library.pattern.strategy.SearchType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        logger.info("═══════════════════════════════════════════════════");
        logger.info("      Library Management System — Demo      ");
        logger.info("═══════════════════════════════════════════════════");

        LibrarySystem library = new LibrarySystem();

        LibraryBranch puneBranch =
                new LibraryBranch("B001", "Pune Central Library", "FC Road, Pune");

        LibraryBranch mumbaiBranch =
                new LibraryBranch("B002", "Mumbai Knowledge Hub", "Andheri West, Mumbai");

        library.addBranch(puneBranch);
        library.addBranch(mumbaiBranch);


        Book wingsOfFire = BookFactory.createBook(
                "ISBN-IND-001",
                "Wings of Fire",
                "A.P.J. Abdul Kalam",
                1999,
                Book.Genre.BIOGRAPHY
        );

        // Changed BUSINESS -> NON_FICTION because Book.Genre.BUSINESS doesn't exist
        Book richDad = BookFactory.createBook(
                "ISBN-IND-002",
                "Rich Dad Poor Dad",
                "Robert Kiyosaki",
                1997,
                Book.Genre.NON_FICTION
        );

        Book discoveryIndia = BookFactory.createBook(
                "ISBN-IND-003",
                "The Discovery of India",
                "Jawaharlal Nehru",
                1946,
                Book.Genre.HISTORY
        );

        Book chanakya = BookFactory.createBook(
                "ISBN-IND-004",
                "Chanakya Neeti",
                "Chanakya",
                300,
                Book.Genre.PHILOSOPHY
        );

        Book ikigai = BookFactory.createBook(
                "ISBN-IND-005",
                "Ikigai",
                "Francesc Miralles",
                2016,
                Book.Genre.NON_FICTION
        );

        library.addBook(wingsOfFire, "B001");
        library.addBook(richDad, "B001");
        library.addBook(discoveryIndia, "B001");
        library.addBook(chanakya, "B001");
        library.addBook(ikigai, "B002");


        Patron rahul = new Patron(
                "P001",
                "Rahul Sharma",
                "rahul.sharma@email.com",
                "9876543210"
        );

        Patron priya = new Patron(
                "P002",
                "Priya Verma",
                "priya.verma@email.com",
                "9876543211"
        );

        Patron arjun = new Patron(
                "P003",
                "Arjun Patil",
                "arjun.patil@email.com",
                "9876543212"
        );

        library.registerPatron(rahul);
        library.registerPatron(priya);
        library.registerPatron(arjun);


        logger.info("\n--- Book Search Demo ---");

        List<Book> historyBooks =
                library.getBookService().search(SearchType.GENRE, "HISTORY");

        logger.info("History books found: {}", historyBooks.size());

        historyBooks.forEach(book ->
                logger.info("  » {}", book)
        );

        List<Book> kalamBooks =
                library.getBookService().search(SearchType.AUTHOR, "Kalam");

        logger.info("Books by Dr. Kalam: {}", kalamBooks.size());


        logger.info("\n--- Checkout Demo ---");

        LendingRecord rahulBook =
                library.checkout("ISBN-IND-001", "P001", "B001");

        logger.info("Rahul checked out Wings of Fire: {}", rahulBook);

        LendingRecord rahulSecondBook =
                library.checkout("ISBN-IND-002", "P001", "B001");

        logger.info("Rahul checked out Rich Dad Poor Dad: {}", rahulSecondBook);

        LendingRecord priyaBook =
                library.checkout("ISBN-IND-003", "P002", "B001");

        logger.info("Priya checked out The Discovery of India: {}", priyaBook);


        logger.info("\n--- Reservation Demo ---");

        Reservation priyaReservation =
                library.reserveBook("ISBN-IND-001", "P002", "B001");

        logger.info("Priya reserved Wings of Fire: {}", priyaReservation);


        logger.info("\n--- Return Demo (Notification Trigger) ---");

        LendingRecord returnedBook =
                library.returnBook("ISBN-IND-001", "P001", "B001");

        logger.info("Rahul returned Wings of Fire: {}", returnedBook);


        logger.info("\n--- Patron Update Demo ---");

        library.getPatronService().updatePatron(
                "P001",
                "Rahul Sharma Patil",
                "rahul.patil@email.com",
                "9999999999"
        );

        library.getPatronService().findPatronById("P001")
                .ifPresentOrElse(
                        p -> logger.info("Rahul's updated info: {}", p),
                        () -> logger.warn("Patron not found: P001")
                );


        logger.info("\n--- Borrowing History ---");

        library.getPatronService().getBorrowingHistory("P001")
                .forEach(record ->
                        logger.info("  Rahul history: {}", record)
                );

        logger.info("\n--- Branch Transfer Demo ---");

        library.returnBook("ISBN-IND-003", "P002", "B001");

        library.transferBook("ISBN-IND-003", "B001", "B002");

        logger.info("The Discovery of India transferred from Pune to Mumbai branch");


        logger.info("\n--- Recommendation Demo ---");

        library.checkout("ISBN-IND-004", "P001", "B001");

        List<Book> recommendations =
                library.getRecommendations("P001");

        logger.info("Recommendations for Rahul ({} results):",
                recommendations.size());

        recommendations.forEach(book ->
                logger.info("  ★ {}", book)
        );

        logger.info("\n--- Inventory Summary ---");

        logger.info("Pune branch — available books: {}",
                library.getBranchService().getAvailableBooksByBranch("B001").size());

        logger.info("Pune branch — borrowed books: {}",
                library.getBranchService().getBorrowedBooksByBranch("B001").size());

        logger.info("\n═══════════════════════════════════════════════════");
        logger.info("              Demo Completed Successfully          ");
        logger.info("═══════════════════════════════════════════════════");
    }
}