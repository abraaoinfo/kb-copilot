package com.example.kb.controller;

import com.example.kb.domain.Documento;
import com.example.kb.repository.DocumentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DocumentListController {
    
    private static final Logger logger = LoggerFactory.getLogger(DocumentListController.class);
    
    private final DocumentRepository documentRepository;
    
    public DocumentListController(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }
    
    @GetMapping("/docs")
    public String showDocuments(Model model) {
        try {
            List<Documento> documents = documentRepository.findAll();
            model.addAttribute("documents", documents);
            model.addAttribute("count", documents.size());
            
            logger.info("Exibindo {} documentos", documents.size());
            return "document-list";
            
        } catch (Exception e) {
            logger.error("Erro ao listar documentos", e);
            model.addAttribute("error", "Erro ao carregar documentos: " + e.getMessage());
            return "document-list";
        }
    }
}


