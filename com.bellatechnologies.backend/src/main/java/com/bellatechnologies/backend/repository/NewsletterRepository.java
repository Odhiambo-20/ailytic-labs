package com.bellatechnologies.backend.repository;

import com.bellatechnologies.backend.model.Newsletter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsletterRepository extends JpaRepository<Newsletter, String> {}
