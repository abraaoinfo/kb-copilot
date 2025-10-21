package com.example.kb.controller;

import com.example.kb.service.RagService;
import com.example.kb.service.RateLimitService;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(AskController.class)
@ActiveProfiles("test")
class AskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RagService ragService;

    @MockBean
    private RateLimitService rateLimitService;

    @Test
    @WithMockUser
    void shouldShowAskPage() throws Exception {
        mockMvc.perform(get("/ask"))
                .andExpect(status().isOk())
                .andExpect(view().name("ask"))
                .andExpect(model().attributeExists("question"));
    }

    @Test
    @WithMockUser
    void shouldProcessQuestionWithGet() throws Exception {
        // Mock rate limit
        when(rateLimitService.tryConsume("ask")).thenReturn(true);
        
        // Mock RAG service
        when(ragService.processQuestion(anyString())).thenReturn("Esta é uma resposta de teste");
        
        // Mock citations
        Document citation = new Document("Conteúdo de teste");
        citation.getMetadata().put("title", "Documento Teste");
        citation.getMetadata().put("type", "TEXT");
        when(ragService.findSimilarDocuments(anyString(), anyInt())).thenReturn(List.of(citation));

        mockMvc.perform(get("/ask")
                .param("question", "Qual é a missão?"))
                .andExpect(status().isOk())
                .andExpect(view().name("result"))
                .andExpect(model().attributeExists("question"))
                .andExpect(model().attributeExists("response"))
                .andExpect(model().attributeExists("citations"));
    }

    @Test
    @WithMockUser
    void shouldProcessQuestionWithPost() throws Exception {
        // Mock rate limit
        when(rateLimitService.tryConsume("ask")).thenReturn(true);
        
        // Mock RAG service
        when(ragService.processQuestion(anyString())).thenReturn("Esta é uma resposta de teste");
        
        // Mock citations
        Document citation = new Document("Conteúdo de teste");
        citation.getMetadata().put("title", "Documento Teste");
        citation.getMetadata().put("type", "TEXT");
        when(ragService.findSimilarDocuments(anyString(), anyInt())).thenReturn(List.of(citation));

        mockMvc.perform(post("/ask")
                .param("question", "Qual é a missão?")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("result"))
                .andExpect(model().attributeExists("question"))
                .andExpect(model().attributeExists("response"))
                .andExpect(model().attributeExists("citations"));
    }

    @Test
    @WithMockUser
    void shouldHandleRateLimitExceeded() throws Exception {
        // Mock rate limit exceeded
        when(rateLimitService.tryConsume("ask")).thenReturn(false);

        mockMvc.perform(post("/ask")
                .param("question", "Qual é a missão?")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("ask"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attributeExists("question"));
    }

    @Test
    @WithMockUser
    void shouldHandleEmptyQuestion() throws Exception {
        mockMvc.perform(post("/ask")
                .param("question", "")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("ask"))
                .andExpect(model().attributeExists("question"));
    }
}


