package com.allyticlabs.backend.service;

import com.allyticlabs.backend.model.Newsletter;
import com.allyticlabs.backend.repository.NewsletterRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NewsletterService {
    private final NewsletterRepository newsletterRepository;

    public NewsletterService(NewsletterRepository newsletterRepository) {
        this.newsletterRepository = newsletterRepository;
    }

    public Newsletter subscribe(Newsletter newsletter) {
        return newsletterRepository.save(newsletter);
    }

    public List<Newsletter> getAllSubscribers() {
        return newsletterRepository.findAll();
    }
}