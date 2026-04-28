package com.example.rag_chatbot.util;

import org.springframework.ai.document.Document;

import java.util.List;
import java.util.stream.Collectors;

public final class PromptBuilder {

    private PromptBuilder(){}

    public static String buildRagPrompt(String userQuestion, List<Document> contextDocs){
        String context = contextDocs.stream()
                .map(doc ->{
                    Object src = doc.getMetadata().getOrDefault("source", "unknown");
                    Object idx = doc.getMetadata().getOrDefault("chunkIndex", -1);
                    return "[source=%s, chunk=%s]\n%s".formatted(src, idx, doc.getText());
                })
                .collect(Collectors.joining("\n\n---\n\n"));

        return """
                You are a RAG assistant for a Java Spring Boot developer.

                Rules:
                1) Answer primarily from the provided context.
                2) If context is insufficient, clearly say what is missing.
                3) Keep answer practical and concise.
                4) Cite source and chunk in plain text when relevant.

                User Question:
                %s

                Retrieved Context:
                %s
                """.formatted(userQuestion, context);
    }
}
