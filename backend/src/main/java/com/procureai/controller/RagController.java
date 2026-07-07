package com.procureai.controller;

import com.procureai.domain.Document;
import com.procureai.repository.DocumentRepository;
import com.procureai.ai.GroqClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.*;

@RestController
@RequestMapping("/api/rag")
public class RagController {
    private final DocumentRepository documentRepository;
    private final GroqClient groqClient;

    public RagController(DocumentRepository documentRepository, GroqClient groqClient) {
        this.documentRepository = documentRepository;
        this.groqClient = groqClient;
    }

    @PostMapping("/ingest")
    public Map<String, Object> ingest(@RequestParam("file") MultipartFile file) {
        try {
            String content = new String(file.getBytes());
            Document doc = new Document();
            doc.setFilename(file.getOriginalFilename());
            doc.setContent(content);
            doc.setContentType(file.getContentType());
            documentRepository.save(doc);
            return Map.of("message", "Document ingested", "filename", doc.getFilename(), "id", doc.getId());
        } catch (Exception e) {
            return Map.of("error", e.getMessage());
        }
    }

    @PostMapping("/ask")
    public Map<String, Object> ask(@RequestBody Map<String, String> request) {
        String question = request.get("question");
        List<Document> docs = documentRepository.findAll();

        StringBuilder context = new StringBuilder();
        for (Document doc : docs) {
            String content = doc.getContent();
            if (content.length() > 2000) content = content.substring(0, 2000);
            context.append("--- Document: ").append(doc.getFilename()).append(" ---\n");
            context.append(content).append("\n\n");
        }

        String systemPrompt = "You are a RAG (Retrieval-Augmented Generation) assistant. " +
            "Answer questions based ONLY on the provided context. If the context doesn't contain the answer, " +
            "say 'I cannot find this information in the uploaded documents.' Include citations." +
            "\n\nCONTEXT:\n" + (context.isEmpty() ? "No documents uploaded yet." : context.toString());

        String response = groqClient.chat(systemPrompt, question);
        return Map.of("response", response, "documentsUsed", docs.size());
    }
}
