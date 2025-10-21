package com.example.kb.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {
    
    @GetMapping("/admin")
    public String testAdmin() {
        return "Admin endpoint is working!";
    }
    
    @GetMapping("/public")
    public String testPublic() {
        return "Public endpoint is working!";
    }
}


