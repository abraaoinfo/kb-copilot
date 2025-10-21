package com.example.kb.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "documentos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Documento {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 500)
    private String titulo;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String conteudo;
    
    @Column(name = "tipo_fonte", length = 50)
    private String tipoFonte; // TEXTO, PDF, etc.
    
    @Column(name = "nome_fonte", length = 255)
    private String nomeFonte;
    
    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();
    
    @Column(name = "vector_id", length = 255)
    private String vectorId; // ID do documento no vector store
    
    public Documento() {
        // Construtor padrão para Hibernate
    }
    
    public Documento(String titulo, String conteudo, String tipoFonte, String nomeFonte) {
        this.titulo = titulo;
        this.conteudo = conteudo;
        this.tipoFonte = tipoFonte;
        this.nomeFonte = nomeFonte;
        this.criadoEm = LocalDateTime.now();
    }
    
    // Getters manuais para garantir compatibilidade
    public Long getId() {
        return id;
    }
    
    public String getTitulo() {
        return titulo;
    }
    
    public String getConteudo() {
        return conteudo;
    }
    
    public String getTipoFonte() {
        return tipoFonte;
    }
    
    public String getNomeFonte() {
        return nomeFonte;
    }
    
    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }
    
    public String getVectorId() {
        return vectorId;
    }
    
    // Setters manuais
    public void setId(Long id) {
        this.id = id;
    }
    
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    
    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }
    
    public void setTipoFonte(String tipoFonte) {
        this.tipoFonte = tipoFonte;
    }
    
    public void setNomeFonte(String nomeFonte) {
        this.nomeFonte = nomeFonte;
    }
    
    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }
    
    public void setVectorId(String vectorId) {
        this.vectorId = vectorId;
    }
}
