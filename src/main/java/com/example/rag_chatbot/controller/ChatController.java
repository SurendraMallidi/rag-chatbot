package com.example.rag_chatbot.controller;

import com.example.rag_chatbot.model.dto.ChatRequest;
import com.example.rag_chatbot.model.dto.ChatResponse;
import com.example.rag_chatbot.service.ChatService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@AllArgsConstructor
@RequestMapping("/api")
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/chat")
    public ChatResponse chat(@Valid @RequestBody ChatRequest request){
        String answer = chatService.ask(request.question());
        return new ChatResponse(answer);
    }
}
