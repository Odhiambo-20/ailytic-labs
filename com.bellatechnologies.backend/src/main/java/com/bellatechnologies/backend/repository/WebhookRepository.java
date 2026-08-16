package com.bellatechnologies.backend.repository;

import com.bellatechnologies.backend.model.Webhook;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WebhookRepository extends JpaRepository<Webhook, String> {
    List<Webhook> findByProvider(String provider);

    default List<Webhook> findByProvider(String provider, int limit) {
        return findByProvider(provider).stream().limit(limit).toList();
    }

    default List<Webhook> findAll(int limit) {
        return findAll(PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "timestamp"))).getContent();
    }

    default void delete(String id) {
        deleteById(id);
    }
}
