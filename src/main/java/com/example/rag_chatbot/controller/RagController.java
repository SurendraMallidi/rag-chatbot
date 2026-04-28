package com.example.rag_chatbot.controller;

import com.example.rag_chatbot.model.dto.RagAskRequest;
import com.example.rag_chatbot.model.dto.RagAskResponse;
import com.example.rag_chatbot.service.RagService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rag")
@AllArgsConstructor
public class RagController {

    private final RagService ragService;

    @PostMapping("/ask")
    public RagAskResponse ask(@Valid @RequestBody RagAskRequest request) {
        return ragService.ask(request.question());
    }

}
