package com.example.rag_chatbot.controller;

import com.example.rag_chatbot.model.dto.UploadResponse;
import com.example.rag_chatbot.service.IngestionService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/documents")
@AllArgsConstructor
public class DocumentController {

    private final IngestionService ingestionService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UploadResponse upload(@RequestPart("file")MultipartFile file) throws Exception{
        int chunks = ingestionService.ingest(file);
        return new UploadResponse(file.getOriginalFilename(),
                chunks, "Document ingested successfully");
    }
}
