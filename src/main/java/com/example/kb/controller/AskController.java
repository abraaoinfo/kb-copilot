package com.example.kb.controller;

import com.example.kb.service.RagServiceInterface;
import com.example.kb.service.RateLimitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
public class AskController {
    
    private static final Logger logger = LoggerFactory.getLogger(AskController.class);
    
    private final RagServiceInterface ragService;
    private final RateLimitService rateLimitService;

    public AskController(RagServiceInterface ragService, RateLimitService rateLimitService) {
        this.ragService = ragService;
        this.rateLimitService = rateLimitService;
    }
    
    @GetMapping("/ask")
    public String askPage(@RequestParam(required = false) String question, Model model) {
        // Adiciona requestId ao MDC
        String requestId = "req-" + UUID.randomUUID().toString().substring(0, 8);
        MDC.put("requestId", requestId);
        
        try {
            if (question != null && !question.trim().isEmpty()) {
                return processQuestion(question, model);
            }
            
            model.addAttribute("question", "");
            return "ask";
            
        } finally {
            MDC.clear();
        }
    }
    
    @PostMapping("/ask")
    public String processQuestion(@RequestParam String question, Model model) {
        // Adiciona requestId ao MDC
        String requestId = "req-" + UUID.randomUUID().toString().substring(0, 8);
        MDC.put("requestId", requestId);
        
        try {
            logger.info("Processando pergunta: {}", question);
            
            // Verifica rate limit
            if (!rateLimitService.tryConsume("ask")) {
                logger.warn("Rate limit exceeded para /ask");
                model.addAttribute("error", "Muitas consultas. Aguarde um momento antes de tentar novamente.");
                model.addAttribute("question", question);
                return "ask";
            }
            
            // Processa a pergunta com RAG
            String response = ragService.processQuestion(question);
            
            // Busca documentos similares para citações
            List<Document> similarDocs = ragService.findSimilarDocuments(question, 3);
            
            model.addAttribute("question", question);
            model.addAttribute("response", response);
            model.addAttribute("citations", similarDocs);
            
            logger.info("Pergunta processada com sucesso");
            return "result";
            
        } catch (Exception e) {
            logger.error("Erro ao processar pergunta", e);
            model.addAttribute("error", "Erro interno: " + e.getMessage());
            model.addAttribute("question", question);
            return "ask";
            
        } finally {
            MDC.clear();
        }
    }
    
    
    public static class AskRequest {
        private String question;
        
        public String getQuestion() {
            return question;
        }
        
        public void setQuestion(String question) {
            this.question = question;
        }
    }
}
