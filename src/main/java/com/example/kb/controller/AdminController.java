package com.example.kb.controller;

import com.example.kb.domain.Documento;
import com.example.kb.repository.DocumentRepository;
import com.example.kb.service.DocumentIngestionServiceInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/admin")
public class AdminController {
    
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    
    private final DocumentRepository documentRepository;
    private final DocumentIngestionServiceInterface ingestionService;
    
    public AdminController(DocumentRepository documentRepository, 
                          DocumentIngestionServiceInterface ingestionService) {
        this.documentRepository = documentRepository;
        this.ingestionService = ingestionService;
    }
    
    @GetMapping
    public String adminPanel(Model model) {
        // Adiciona requestId ao MDC
        String requestId = "req-" + UUID.randomUUID().toString().substring(0, 8);
        MDC.put("requestId", requestId);
        
        try {
            logger.info("Acessando painel admin");
            
            long documentCount = documentRepository.count();
            logger.info("Document count: {}", documentCount);
            
            List<Documento> documents = documentRepository.findAll();
            logger.info("Documents found: {}", documents.size());
            
            model.addAttribute("documentCount", documentCount);
            model.addAttribute("hasDocuments", documentCount > 0);
            model.addAttribute("documents", documents);
            model.addAttribute("requestId", requestId);
            
            logger.info("Model attributes set, returning admin template");
            return "admin";
            
        } catch (Exception e) {
            logger.error("Erro ao acessar painel admin", e);
            model.addAttribute("error", "Erro interno: " + e.getMessage());
            return "admin";
        } finally {
            MDC.clear();
        }
    }
    
    @GetMapping("/documents")
    public String listDocuments(Model model) {
        String requestId = "req-" + UUID.randomUUID().toString().substring(0, 8);
        MDC.put("requestId", requestId);
        
        try {
            var documents = documentRepository.findAll();
            model.addAttribute("documents", documents);
            model.addAttribute("documentCount", documents.size());
            model.addAttribute("requestId", requestId);
            
            return "documents";
            
        } finally {
            MDC.clear();
        }
    }
    
    @PostMapping("/ingest")
    public String ingestDocument(@RequestParam String title,
                                @RequestParam String content,
                                Model model) {
        // Adiciona requestId ao MDC
        String requestId = "req-" + UUID.randomUUID().toString().substring(0, 8);
        MDC.put("requestId", requestId);
        
        try {
            logger.info("Iniciando ingestão de documento: {}", title);
            
            if (title == null || title.trim().isEmpty()) {
                model.addAttribute("error", "Título é obrigatório");
                return adminPanel(model);
            }
            
            if (content == null || content.trim().isEmpty()) {
                model.addAttribute("error", "Conteúdo é obrigatório");
                return adminPanel(model);
            }
            
            // Verifica se já existe documento com o mesmo título
            if (documentRepository.existsByTitulo(title.trim())) {
                model.addAttribute("error", "Já existe um documento com este título");
                return adminPanel(model);
            }
            
            // Ingere o documento
            Documento documento = ingestionService.ingestTextDocument(title.trim(), content.trim());
            
            logger.info("Documento ingerido com sucesso: {} (ID: {})", title, documento.getId());
            model.addAttribute("success", "Documento ingerido com sucesso!");
            
            return adminPanel(model);
            
        } catch (Exception e) {
            logger.error("Erro ao ingerir documento", e);
            model.addAttribute("error", "Erro ao ingerir documento: " + e.getMessage());
            return adminPanel(model);
            
        } finally {
            MDC.clear();
        }
    }
    
    @PostMapping("/ingest-pdf")
    public String ingestPdfDocument(@RequestParam String title,
                                   @RequestParam("file") MultipartFile file,
                                   Model model) {
        // Adiciona requestId ao MDC
        String requestId = "req-" + UUID.randomUUID().toString().substring(0, 8);
        MDC.put("requestId", requestId);
        
        try {
            logger.info("Iniciando ingestão de PDF: {} (arquivo: {})", title, file.getOriginalFilename());
            
            if (title == null || title.trim().isEmpty()) {
                model.addAttribute("error", "Título é obrigatório");
                return adminPanel(model);
            }
            
            if (file.isEmpty()) {
                model.addAttribute("error", "Arquivo PDF é obrigatório");
                return adminPanel(model);
            }
            
            if (!file.getContentType().equals("application/pdf")) {
                model.addAttribute("error", "Arquivo deve ser um PDF");
                return adminPanel(model);
            }
            
            // Verifica se já existe documento com o mesmo título
            if (documentRepository.existsByTitulo(title.trim())) {
                model.addAttribute("error", "Já existe um documento com este título");
                return adminPanel(model);
            }
            
            // Ingere o documento PDF
            Documento documento = ingestionService.ingestPdfDocument(title.trim(), file);
            
            logger.info("PDF ingerido com sucesso: {} (ID: {})", title, documento.getId());
            model.addAttribute("success", "PDF ingerido com sucesso!");
            
            return adminPanel(model);
            
        } catch (Exception e) {
            logger.error("Erro ao ingerir PDF", e);
            model.addAttribute("error", "Erro ao ingerir PDF: " + e.getMessage());
            return adminPanel(model);
            
        } finally {
            MDC.clear();
        }
    }
    
    @GetMapping("/status")
    @ResponseBody
    public String getStatus() {
        try {
            long documentCount = documentRepository.count();
            return String.format("{\"status\":\"ok\",\"documents\":%d}", documentCount);
        } catch (Exception e) {
            logger.error("Erro ao obter status", e);
            return "{\"status\":\"error\",\"message\":\"" + e.getMessage() + "\"}";
        }
    }
    
    @GetMapping("/test")
    @ResponseBody
    public String testAdmin() {
        try {
            long documentCount = documentRepository.count();
            List<Documento> documents = documentRepository.findAll();
            return String.format("Admin test OK. Documents: %d, Found: %d", documentCount, documents.size());
        } catch (Exception e) {
            logger.error("Erro no teste admin", e);
            return "Admin test ERROR: " + e.getMessage();
        }
    }
}
