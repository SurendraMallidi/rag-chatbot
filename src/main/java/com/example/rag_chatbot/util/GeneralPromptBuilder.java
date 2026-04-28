package com.example.rag_chatbot.util;

public final class GeneralPromptBuilder {

    private GeneralPromptBuilder() {}

    public static String buildGeneralPrompt(String userQuestion) {
        return """
                You are a helpful AI assistant for a Java Spring Boot developer.
                Provide a practical, concise, and technically accurate answer.
                If assumptions are needed, state them clearly.

                User Question:
                %s
                """.formatted(userQuestion);
    }
}
