package com.example.kb.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@Controller
@RequestMapping("/login")
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @GetMapping
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                           @RequestParam(value = "logout", required = false) String logout,
                           Model model) {
        MDC.put("requestId", UUID.randomUUID().toString());
        logger.info("GET /login request received. Error: {}, Logout: {}", error, logout);
        
        if (error != null) {
            model.addAttribute("error", "Usuário ou senha inválidos!");
        }
        
        if (logout != null) {
            model.addAttribute("logout", "Você foi desconectado com sucesso!");
        }
        
        model.addAttribute("requestId", MDC.get("requestId"));
        return "login";
    }
}


