package com.example.kb.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@ConditionalOnProperty(name = "spring.ai.openai.api-key", matchIfMissing = false)
public class RagService implements RagServiceInterface {
    
    private static final Logger logger = LoggerFactory.getLogger(RagService.class);
    
    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    
    public RagService(ChatClient chatClient, VectorStore vectorStore) {
        this.chatClient = chatClient;
        this.vectorStore = vectorStore;
    }
    
    public String processQuestion(String question) {
        logger.debug("Processando pergunta RAG: {}", question);
        
        try {
            // Busca documentos similares
            SearchRequest searchRequest = SearchRequest.query(question)
                    .withTopK(8)
                    .withSimilarityThreshold(0.3);
            
            List<Document> similarDocs = vectorStore.similaritySearch(searchRequest);
            
            if (similarDocs.isEmpty()) {
                logger.warn("Nenhum documento similar encontrado para a pergunta: {}", question);
                return "Não encontrei base suficiente para responder sua pergunta. Por favor, verifique se há documentos ingeridos no sistema.";
            }
            
            logger.info("Encontrados {} documentos similares", similarDocs.size());
            
            // Constrói o contexto com os documentos encontrados
            StringBuilder context = new StringBuilder();
            for (int i = 0; i < similarDocs.size(); i++) {
                Document doc = similarDocs.get(i);
                context.append("Documento ").append(i + 1).append(":\n");
                context.append(doc.getContent()).append("\n\n");
            }
            
            // Template para o prompt RAG
            PromptTemplate promptTemplate = new PromptTemplate("""
                Você é um assistente IA especializado em responder perguntas baseadas no conhecimento fornecido.
                
                Contexto dos documentos:
                {context}
                
                Pergunta do usuário: {question}
                
                Instruções:
                1. Responda baseado APENAS no contexto fornecido
                2. Se a informação não estiver no contexto, diga que não tem informação suficiente
                3. Seja claro e objetivo
                4. Cite os números dos documentos quando relevante
                
                Resposta:
                """);
            
            Prompt prompt = promptTemplate.create(Map.of(
                "context", context.toString(),
                "question", question
            ));
            
            String response = chatClient.prompt(prompt).call().content();
            
            logger.info("Pergunta processada com sucesso");
            return response;
            
        } catch (Exception e) {
            logger.error("Erro ao processar pergunta RAG", e);
            return "Desculpe, ocorreu um erro ao processar sua pergunta. Tente novamente mais tarde.";
        }
    }
    
    public List<Document> findSimilarDocuments(String query, int topK) {
        SearchRequest searchRequest = SearchRequest.query(query)
                .withTopK(topK)
                .withSimilarityThreshold(0.3);
        
        return vectorStore.similaritySearch(searchRequest);
    }
}
