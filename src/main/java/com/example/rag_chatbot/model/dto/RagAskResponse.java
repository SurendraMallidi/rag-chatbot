package com.example.rag_chatbot.model.dto;

import java.util.List;

public record RagAskResponse(
        String answer,
        String answerMode,       // "PRIVATE_RAG" or "GENERAL_LLM"
        List<SourceDto> sources,
        int retrievedChunks
) {
}
