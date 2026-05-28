package com.library.pattern.observer;


public enum BookEvent {
    BOOK_RETURNED,       // A borrowed book was returned
    BOOK_AVAILABLE,      // A reserved book became available
    RESERVATION_EXPIRED, // A reservation expired without being picked up
    BOOK_TRANSFERRED,    // A book was transferred between branches
    BOOK_OVERDUE         // A book is overdue for return
}
