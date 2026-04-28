package com.example.rag_chatbot.service;

import lombok.AllArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class RetrievalService {

    private final VectorStore vectorStore;

    public List<Document> retrievalRelevantChunks(String question, int topK, double similarityThreshold){
        SearchRequest request = SearchRequest.builder()
                .query(question)
                .topK(topK)
                .similarityThreshold(similarityThreshold)
                .build();

        return vectorStore.similaritySearch(request);
    }
}
