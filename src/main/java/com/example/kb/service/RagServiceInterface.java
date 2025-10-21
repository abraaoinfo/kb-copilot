package com.example.kb.service;

import org.springframework.ai.document.Document;

import java.util.List;

public interface RagServiceInterface {
    String processQuestion(String question);
    List<Document> findSimilarDocuments(String query, int topK);
}


