package com.example.kb.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(1)
public class MdcFilter implements Filter {
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        
        // Gera um requestId único se não existir
        String requestId = httpRequest.getHeader("X-Request-ID");
        if (requestId == null || requestId.trim().isEmpty()) {
            requestId = "req-" + UUID.randomUUID().toString().substring(0, 8);
        }
        
        // Adiciona ao MDC
        MDC.put("requestId", requestId);
        
        try {
            chain.doFilter(request, response);
        } finally {
            // Remove do MDC após o processamento
            MDC.clear();
        }
    }
}


