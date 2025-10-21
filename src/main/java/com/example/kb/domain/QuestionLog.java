package com.example.kb.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "question_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String pergunta;
    
    @Column(columnDefinition = "TEXT")
    private String resposta;
    
    @Column(name = "request_id", length = 100)
    private String requestId;
    
    @Column(name = "ip_address", length = 45)
    private String ipAddress;
    
    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;
    
    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();
    
    @Column(name = "tempo_resposta_ms")
    private Long tempoRespostaMs;
    
    @Column(name = "sucesso", nullable = false)
    private Boolean sucesso = true;
    
    @Column(name = "erro_mensagem", columnDefinition = "TEXT")
    private String erroMensagem;
    
    public QuestionLog() {
        // Construtor padrão para Hibernate
    }
    
    public QuestionLog(String pergunta, String requestId, String ipAddress) {
        this.pergunta = pergunta;
        this.requestId = requestId;
        this.ipAddress = ipAddress;
        this.criadoEm = LocalDateTime.now();
    }
}
