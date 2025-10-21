package com.example.kb.service;

import com.example.kb.domain.Documento;
import com.example.kb.repository.DocumentRepository;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@ConditionalOnProperty(name = "spring.ai.openai.api-key", matchIfMissing = false)
public class DocumentIngestionService implements DocumentIngestionServiceInterface {
    
    private static final Logger logger = LoggerFactory.getLogger(DocumentIngestionService.class);
    
    private final DocumentRepository documentRepository;
    private final VectorStore vectorStore;
    private final Tika tika;
    
    public DocumentIngestionService(DocumentRepository documentRepository, VectorStore vectorStore) {
        this.documentRepository = documentRepository;
        this.vectorStore = vectorStore;
        this.tika = new Tika();
    }
    
    public Documento ingestTextDocument(String title, String content) {
        logger.info("Ingerindo documento de texto: {}", title);
        
        // Salva o documento no banco
        Documento documento = new Documento(title, content, "TEXT", null);
        
        Documento savedDocument = documentRepository.save(documento);
        
        // Cria o documento para o vector store
        Document aiDocument = new Document(content);
        aiDocument.getMetadata().put("id", savedDocument.getId().toString());
        aiDocument.getMetadata().put("title", title);
        aiDocument.getMetadata().put("type", "TEXT");
        aiDocument.getMetadata().put("created_at", savedDocument.getCriadoEm().toString());
        
        // Adiciona ao vector store
        vectorStore.add(List.of(aiDocument));
        
        logger.info("Documento de texto ingerido com sucesso: {} (ID: {})", title, savedDocument.getId());
        return savedDocument;
    }
    
    public Documento ingestPdfDocument(String title, MultipartFile file) throws IOException, TikaException {
        logger.info("Ingerindo documento PDF: {} (arquivo: {})", title, file.getOriginalFilename());
        
        // Extrai o texto do PDF
        String content = tika.parseToString(file.getInputStream());
        
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Não foi possível extrair texto do PDF");
        }
        
        // Salva o documento no banco
        Documento documento = new Documento(title, content, "PDF", file.getOriginalFilename());
        
        Documento savedDocument = documentRepository.save(documento);
        
        // Cria o documento para o vector store
        Document aiDocument = new Document(content);
        aiDocument.getMetadata().put("id", savedDocument.getId().toString());
        aiDocument.getMetadata().put("title", title);
        aiDocument.getMetadata().put("type", "PDF");
        aiDocument.getMetadata().put("filename", file.getOriginalFilename());
        aiDocument.getMetadata().put("created_at", savedDocument.getCriadoEm().toString());
        
        // Adiciona ao vector store
        vectorStore.add(List.of(aiDocument));
        
        logger.info("Documento PDF ingerido com sucesso: {} (ID: {})", title, savedDocument.getId());
        return savedDocument;
    }
}
