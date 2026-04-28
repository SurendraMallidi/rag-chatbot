package com.example.rag_chatbot.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Locale;

@Service
public class DocumentParserService {

    public String parse(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename() == null ? "" : file.getOriginalFilename();
        String lower = fileName.toLowerCase(Locale.ROOT);
        if (lower.endsWith(".pdf")){
            return parsePdf(file.getBytes());
        }else if(lower.endsWith(".txt")){
            return parseDocx(file.getBytes());
        }else throw new IllegalArgumentException("Unsupported file type. Allowed: pdf, txt, docx");
    }

    private String parsePdf(byte[] bytes) throws IOException{
        try(var doc = Loader.loadPDF(bytes)){
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(doc);
        }
    }

    private String parseDocx(byte[] bytes) throws IOException{
        try(var bias = new ByteArrayInputStream(bytes);
        var doc = new XWPFDocument(bias)){
            StringBuilder sb = new StringBuilder();
            doc.getParagraphs().forEach( p -> sb.append(p.getText()).append("\n"));
            return sb.toString();
        }
    }




}
