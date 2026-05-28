package com.library.service;

import com.library.model.Book;

import java.util.List;

public interface RecommendationService {

    public List<Book> recommend(String patronId, int limit);
    public List<Book> recommend(String patronId);

}
