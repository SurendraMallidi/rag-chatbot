package com.example.rag_chatbot.service;

import lombok.AllArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    private final ChatClient chatClient;

    public ChatService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public String ask(String question){

        return chatClient.prompt()
                .system(
                        """
                                You are a helpful AI assistant for a Java Spring Boot developer.
                                Keep answers practical, concise, and technically accurate.
                                """)
                .user(question)
                .call()
                .content();
    }
}
