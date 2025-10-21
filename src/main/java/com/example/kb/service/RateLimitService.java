package com.example.kb.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Service
public class RateLimitService {
    
    private static final Logger logger = LoggerFactory.getLogger(RateLimitService.class);
    
    private final Map<String, RequestCounter> counters = new ConcurrentHashMap<>();
    
    @Value("${rate-limit.ask.capacity:10}")
    private int capacity;
    
    @Value("${rate-limit.ask.refill-period:60}")
    private int refillPeriodSeconds;
    
    public boolean tryConsume(String endpoint) {
        RequestCounter counter = counters.computeIfAbsent(endpoint, k -> new RequestCounter());
        
        LocalDateTime now = LocalDateTime.now();
        
        // Reset counter if period has passed
        if (counter.lastReset.plusSeconds(refillPeriodSeconds).isBefore(now)) {
            counter.reset();
        }
        
        if (counter.count >= capacity) {
            logger.warn("Rate limit exceeded for endpoint: {}. Count: {}", endpoint, counter.count);
            return false;
        }
        
        counter.count++;
        counter.lastReset = now;
        return true;
    }
    
    public long getAvailableTokens(String endpoint) {
        RequestCounter counter = counters.get(endpoint);
        return counter != null ? Math.max(0, capacity - counter.count) : capacity;
    }
    
    private static class RequestCounter {
        int count = 0;
        LocalDateTime lastReset = LocalDateTime.now();
        
        void reset() {
            count = 0;
            lastReset = LocalDateTime.now();
        }
    }
}
