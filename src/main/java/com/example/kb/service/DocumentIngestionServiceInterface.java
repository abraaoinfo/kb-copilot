package com.example.kb.service;

import com.example.kb.domain.Documento;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import org.apache.tika.exception.TikaException;

public interface DocumentIngestionServiceInterface {
    Documento ingestTextDocument(String title, String content);
    Documento ingestPdfDocument(String title, MultipartFile file) throws IOException, TikaException;
}
