package com.example.kb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class SpringAiRagApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringAiRagApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void validateConfiguration(ApplicationReadyEvent event) {
        Environment environment = event.getApplicationContext().getEnvironment();
        String[] activeProfiles = environment.getActiveProfiles();
        
        // Skip validation in dev and test profiles (uses mock service)
        for (String profile : activeProfiles) {
            if ("dev".equals(profile) || "test".equals(profile)) {
                System.out.println("✓ " + profile.toUpperCase() + " profile active - skipping API key validation");
                System.out.println("✓ Application started successfully (MOCK MODE)");
                return;
            }
        }
        
        String apiKey = environment.getProperty("OPENAI_API_KEY");
        
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalStateException(
                "OPENAI_API_KEY environment variable is required but not set. " +
                "Please set it with: export OPENAI_API_KEY=your_api_key_here"
            );
        }
        
        if (apiKey.length() < 10) {
            throw new IllegalStateException(
                "OPENAI_API_KEY appears to be invalid (too short). " +
                "Please verify your OpenAI API key."
            );
        }
        
        System.out.println("✓ OPENAI_API_KEY validated successfully");
        System.out.println("✓ Application started successfully");
    }
}