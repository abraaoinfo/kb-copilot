package com.example.kb.controller;

import com.example.kb.domain.Documento;
import com.example.kb.repository.DocumentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class DocumentController {
    
    private static final Logger logger = LoggerFactory.getLogger(DocumentController.class);
    
    private final DocumentRepository documentRepository;
    
    public DocumentController(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }
    
    @GetMapping("/documents")
    public List<Documento> listDocuments() {
        String requestId = "req-" + UUID.randomUUID().toString().substring(0, 8);
        MDC.put("requestId", requestId);
        
        try {
            logger.info("Listando todos os documentos");
            List<Documento> documents = documentRepository.findAll();
            logger.info("Encontrados {} documentos", documents.size());
            return documents;
            
        } finally {
            MDC.clear();
        }
    }
    
    @GetMapping("/documents/count")
    public long getDocumentCount() {
        String requestId = "req-" + UUID.randomUUID().toString().substring(0, 8);
        MDC.put("requestId", requestId);
        
        try {
            long count = documentRepository.count();
            logger.info("Total de documentos: {}", count);
            return count;
            
        } finally {
            MDC.clear();
        }
    }
}


