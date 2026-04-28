package com.example.rag_chatbot.service;

import com.example.rag_chatbot.model.dto.RagAskResponse;
import com.example.rag_chatbot.model.dto.SourceDto;
import com.example.rag_chatbot.util.GeneralPromptBuilder;
import com.example.rag_chatbot.util.PromptBuilder;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class RagService {

    private static final String MODE_PRIVATE_RAG = "PRIVATE_RAG";
    private static final String MODE_GENERAL_LLM = "GENERAL_LLM";

    private final RetrievalService retrievalService;
    private final ChatClient chatClient;

    public RagService(RetrievalService retrievalService, ChatClient.Builder chatClientBuilder) {
        this.retrievalService = retrievalService;
        this.chatClient = chatClientBuilder.build();
    }

    public RagAskResponse ask(String question) {
        int topK = 5;
        double similarityThreshold = 0.65;

        List<Document> retrievedDocs = retrievalService.retrievalRelevantChunks(question, topK, similarityThreshold);

        // Strategy: if relevant chunks found -> private RAG, else -> general LLM
        if (retrievedDocs != null && !retrievedDocs.isEmpty()) {
            return answerFromPrivateDocs(question, retrievedDocs);
        } else {
            return answerFromGeneralKnowledge(question);
        }
    }

    private RagAskResponse answerFromPrivateDocs(String question, List<Document> docs) {
        String ragPrompt = PromptBuilder.buildRagPrompt(question, docs);

        String answer = chatClient.prompt()
                .user(ragPrompt)
                .call()
                .content();

        List<SourceDto> sources = docs.stream()
                .map(doc -> new SourceDto(
                        String.valueOf(doc.getMetadata().getOrDefault("source", "unknown")),
                        parseChunkIndex(doc.getMetadata().get("chunkIndex")),
                        buildPreview(doc.getText())
                ))
                .toList();

        return new RagAskResponse(
                answer,
                MODE_PRIVATE_RAG,
                sources,
                docs.size()
        );
    }

    private RagAskResponse answerFromGeneralKnowledge(String question) {
        String generalPrompt = GeneralPromptBuilder.buildGeneralPrompt(question);

        String answer = chatClient.prompt()
                .user(generalPrompt)
                .call()
                .content();

        return new RagAskResponse(
                answer,
                MODE_GENERAL_LLM,
                Collections.emptyList(),
                0
        );
    }

    private Integer parseChunkIndex(Object value) {
        if (value == null) return null;
        if (value instanceof Integer i) return i;
        try {
            return Integer.parseInt(value.toString());
        } catch (Exception e) {
            return null;
        }
    }

    private String buildPreview(String text) {
        if (text == null) return "";
        return text.length() <= 180 ? text : text.substring(0, 180) + "...";
    }
}
