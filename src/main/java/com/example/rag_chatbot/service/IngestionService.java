package com.example.rag_chatbot.service;

import lombok.AllArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class IngestionService {

    private final DocumentParserService documentParserService;
    private final VectorStore vectorStore;

    public int ingest(MultipartFile file) throws IOException{

        String rawText = documentParserService.parse(file);
        if((rawText == null) || rawText.isBlank())
            throw new IllegalArgumentException("Extracted text is empty");

        String fileName = file.getOriginalFilename() == null ? "unknown" : file.getOriginalFilename();

        Document rootDoc = new Document(rawText, Map.of("fileName", fileName));

        TokenTextSplitter splitter = new TokenTextSplitter(400, 80, 10, 2000, true);
        List<Document> chunks = splitter.apply(List.of(rootDoc));

        List<Document> enriched = new ArrayList<>();
        for(int i=0;i< chunks.size(); i++){
            Document c = chunks.get(i);
            c.getMetadata().put("chunkIndex", i);
            c.getMetadata().put("source", fileName);
            enriched.add(c);
        }

        vectorStore.add(enriched);
        return enriched.size();

    }
}
