package com.library.pattern.observer;


public interface LibraryObserver {

    void onEvent(BookEvent event, String isbn, String patronId, String branchId);
}
