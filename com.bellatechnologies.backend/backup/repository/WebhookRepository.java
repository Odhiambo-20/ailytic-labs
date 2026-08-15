import java.time.Instant;
package com.bellatechnologies.backend.repository;

import com.bellatechnologies.backend.model.Webhook;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WebhookRepository {
    Webhook save(Webhook webhook);
    Optional<Webhook> findById(String id);
    List<Webhook> findByProvider(String provider, int limit);
    List<Webhook> findAll(int limit);
    void delete(String id);
}
