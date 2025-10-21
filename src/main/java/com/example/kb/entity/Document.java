package com.example.kb.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Document {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 500)
    private String title;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    
    @Column(name = "source_type", length = 50)
    private String sourceType;
    
    @Column(name = "source_name", length = 255)
    private String sourceName;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    public Document() {
        // Construtor padrão para Hibernate
    }
    
    public Document(String title, String content, String sourceType, String sourceName) {
        this.title = title;
        this.content = content;
        this.sourceType = sourceType;
        this.sourceName = sourceName;
        this.createdAt = LocalDateTime.now();
    }
}
