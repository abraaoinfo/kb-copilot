package com.example.kb.controller;

import com.example.kb.domain.Documento;
import com.example.kb.repository.DocumentRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/info")
public class DocumentInfoController {
    
    private final DocumentRepository documentRepository;
    
    public DocumentInfoController(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }
    
    @GetMapping("/documents")
    public String getDocumentsInfo() {
        List<Documento> documents = documentRepository.findAll();
        StringBuilder info = new StringBuilder();
        
        info.append("=== DOCUMENTOS NA BASE DE DADOS ===\n");
        info.append("Total: ").append(documents.size()).append(" documentos\n\n");
        
        for (int i = 0; i < documents.size(); i++) {
            Documento doc = documents.get(i);
            info.append("DOCUMENTO ").append(i + 1).append(":\n");
            info.append("ID: ").append(doc.getId()).append("\n");
            info.append("Título: ").append(doc.getTitulo()).append("\n");
            info.append("Tipo: ").append(doc.getTipoFonte()).append("\n");
            info.append("Criado em: ").append(doc.getCriadoEm()).append("\n");
            if (doc.getNomeFonte() != null) {
                info.append("Arquivo: ").append(doc.getNomeFonte()).append("\n");
            }
            info.append("Conteúdo (primeiros 200 chars): ");
            String content = doc.getConteudo();
            if (content.length() > 200) {
                info.append(content.substring(0, 200)).append("...\n");
            } else {
                info.append(content).append("\n");
            }
            info.append("\n");
        }
        
        return info.toString();
    }
}


