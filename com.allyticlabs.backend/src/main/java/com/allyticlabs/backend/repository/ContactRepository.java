package com.allyticlabs.backend.repository;

import com.allyticlabs.backend.model.Contact;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class ContactRepository {
    private final DynamoDbTable<Contact> contactTable;

    public ContactRepository(DynamoDbEnhancedClient enhancedClient) {
        this.contactTable = enhancedClient.table("Contacts", TableSchema.fromBean(Contact.class));
    }

    public Contact save(Contact contact) {
        if (contact.getId() == null) {
            contact.setId(UUID.randomUUID().toString());
            contact.setCreatedAt(LocalDateTime.now());
        }
        contactTable.putItem(contact);
        return contact;
    }

    public List<Contact> findAll() {
        return contactTable.scan().items().stream().collect(Collectors.toList());
    }
}