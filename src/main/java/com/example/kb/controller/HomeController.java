package com.example.kb.controller;

import com.example.kb.domain.Documento;
import com.example.kb.repository.DocumentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.UUID;

@Controller
public class HomeController {
    
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
    
    private final DocumentRepository documentRepository;
    
    public HomeController(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }
    
    @GetMapping("/")
    public String home(Model model) {
        String requestId = "req-" + UUID.randomUUID().toString().substring(0, 8);
        MDC.put("requestId", requestId);
        
        try {
            long documentCount = documentRepository.count();
            List<Documento> documents = documentRepository.findAll();
            
            model.addAttribute("documentCount", documentCount);
            model.addAttribute("documents", documents);
            model.addAttribute("hasDocuments", documentCount > 0);
            
            return "home";
            
        } finally {
            MDC.clear();
        }
    }
}