package com.allyticlabs.backend.repository;

import com.allyticlabs.backend.model.Newsletter;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class NewsletterRepository {
    private final DynamoDbTable<Newsletter> newsletterTable;

    public NewsletterRepository(DynamoDbEnhancedClient enhancedClient) {
        this.newsletterTable = enhancedClient.table("Newsletters", TableSchema.fromBean(Newsletter.class));
    }

    public Newsletter save(Newsletter newsletter) {
        if (newsletter.getSubscribedAt() == null) {
            newsletter.setSubscribedAt(LocalDateTime.now());
        }
        newsletterTable.putItem(newsletter);
        return newsletter;
    }

    public List<Newsletter> findAll() {
        return newsletterTable.scan().items().stream().collect(Collectors.toList());
    }
}