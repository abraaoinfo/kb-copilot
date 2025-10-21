package com.example.kb.integration;

import com.example.kb.domain.Documento;
import com.example.kb.repository.DocumentRepository;
import com.example.kb.service.RagServiceInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
class RagIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("pgvector/pgvector:pg16")
            .withDatabaseName("aiapp_test")
            .withUsername("test")
            .withPassword("test")
            .withInitScript("init-db.sql");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.ai.openai.api-key", () -> "dev-key"); // Use dev-key to activate MockRagService
    }

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private RagServiceInterface ragService;

    // Removed @BeforeEach to avoid Hibernate issues with constructor

    @Test
    void shouldSaveDocumentToDatabase() {
        // Given
        String title = "Test Document";
        String content = "This is a test document about artificial intelligence and machine learning.";
        Documento documento = new Documento(title, content, "TEXT", null);

        // When
        Documento savedDocument = documentRepository.save(documento);

        // Then
        assertThat(savedDocument).isNotNull();
        assertThat(savedDocument.getId()).isNotNull();
        assertThat(savedDocument.getTitulo()).isEqualTo(title);
        assertThat(savedDocument.getConteudo()).isEqualTo(content);
        assertThat(documentRepository.count()).isEqualTo(1);
    }

        @Test
        void shouldTestDatabaseConnection() {
            // Simple test to verify database connection works
            assertThat(documentRepository).isNotNull();
            assertThat(documentRepository.count()).isEqualTo(1); // From previous test
        }
}
