package com.example.kb.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@ConditionalOnProperty(name = "spring.ai.openai.api-key", havingValue = "dev-key")
public class MockRagService implements RagServiceInterface {

    private static final Logger logger = LoggerFactory.getLogger(MockRagService.class);

    public MockRagService() {
        logger.info("MockRagService inicializado para desenvolvimento");
    }

    @Override
    public String processQuestion(String question) {
        try {
            logger.info("Processando pergunta RAG (MOCK): {}", question);

            // Resposta mock simples
            String response = String.format("""
                    [RESPOSTA MOCK] Sistema funcionando em modo de desenvolvimento:
                    
                    Pergunta: %s
                    
                    Esta é uma resposta simulada do sistema RAG. 
                    O sistema está funcionando corretamente em modo de desenvolvimento.
                    
                    Para usar respostas reais da IA, configure uma API key válida da OpenAI.
                    """, question);
            
            logger.info("Pergunta processada com sucesso (MOCK)");
            return response;
            
        } catch (Exception e) {
            logger.error("Erro ao processar pergunta RAG (MOCK): {}", e.getMessage(), e);
            return "Desculpe, ocorreu um erro ao processar sua pergunta.";
        }
    }

    @Override
    public List<Document> findSimilarDocuments(String question, int topK) {
        logger.info("Buscando documentos similares (MOCK) para: {}", question);
        
        // Retorna uma lista vazia para simular que não há documentos
        return new ArrayList<>();
    }
}
