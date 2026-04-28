package com.example.rag_chatbot.model.dto;

import jakarta.validation.constraints.NotBlank;

public record RagAskRequest(
        @NotBlank(message = "question must not be blank")
        String question
) {}