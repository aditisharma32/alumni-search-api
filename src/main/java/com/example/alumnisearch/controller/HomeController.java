package com.example.alumnisearch.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Hidden
public class HomeController {

    /**
     * Redirect root URL ("/") to interactive Swagger UI documentation.
     */
    @GetMapping("/")
    public String index() {
        return "redirect:/swagger-ui.html";
    }
}
