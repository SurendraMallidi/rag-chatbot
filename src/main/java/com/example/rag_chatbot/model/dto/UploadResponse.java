package com.example.rag_chatbot.model.dto;

public record UploadResponse(
        String fileName,
        int chunksStored,
        String message
) {}
