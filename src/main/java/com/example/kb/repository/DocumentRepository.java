package com.example.kb.repository;

import com.example.kb.domain.Documento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepository extends JpaRepository<Documento, Long> {
    boolean existsByTitulo(String titulo);
}
