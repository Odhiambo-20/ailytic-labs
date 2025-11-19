package com.allyticlabs.backend.controller;

import com.allyticlabs.backend.model.Newsletter;
import com.allyticlabs.backend.service.NewsletterService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/newsletter")
public class NewsletterController {
    private final NewsletterService newsletterService;

    public NewsletterController(NewsletterService newsletterService) {
        this.newsletterService = newsletterService;
    }

    @PostMapping
    public ResponseEntity<Newsletter> subscribe(@Valid @RequestBody Newsletter newsletter) {
        Newsletter savedNewsletter = newsletterService.subscribe(newsletter);
        return ResponseEntity.ok(savedNewsletter);
    }

    @GetMapping
    public List<Newsletter> getAllSubscribers() {
        return newsletterService.getAllSubscribers();
    }
}