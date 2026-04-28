package com.example.rag_chatbot.model.dto;

public record SourceDto(
        String source,
        Integer chunkIndex,
        String preview
) {}
