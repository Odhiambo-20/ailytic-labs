package com.allyticlabs.backend.repository;

import com.allyticlabs.backend.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserRepository {

    private final DynamoDbEnhancedClient enhancedClient;
    private static final String TABLE_NAME = "Users";

    private DynamoDbTable<User> getUserTable() {
        return enhancedClient.table(TABLE_NAME, TableSchema.fromBean(User.class));
    }

    public User save(User user) {
        try {
            getUserTable().putItem(user);
            log.info("User saved successfully: {}", user.getUserId());
            return user;
        } catch (Exception e) {
            log.error("Error saving user: {}", e.getMessage());
            throw new RuntimeException("Failed to save user", e);
        }
    }

    public Optional<User> findById(String userId) {
        try {
            Key key = Key.builder().partitionValue(userId).build();
            User user = getUserTable().getItem(key);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            log.error("Error finding user by ID: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<User> findByEmail(String email) {
        try {
            Map<String, AttributeValue> expressionValues = new HashMap<>();
            expressionValues.put(":email", AttributeValue.builder().s(email).build());

            ScanEnhancedRequest scanRequest = ScanEnhancedRequest.builder()
                    .filterExpression(software.amazon.awssdk.enhanced.dynamodb.Expression.builder()
                            .expression("email = :email")
                            .expressionValues(expressionValues)
                            .build())
                    .build();

            return getUserTable().scan(scanRequest)
                    .items()
                    .stream()
                    .findFirst();
        } catch (Exception e) {
            log.error("Error finding user by email: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<User> findByUsername(String username) {
        try {
            Map<String, AttributeValue> expressionValues = new HashMap<>();
            expressionValues.put(":username", AttributeValue.builder().s(username).build());

            ScanEnhancedRequest scanRequest = ScanEnhancedRequest.builder()
                    .filterExpression(software.amazon.awssdk.enhanced.dynamodb.Expression.builder()
                            .expression("username = :username")
                            .expressionValues(expressionValues)
                            .build())
                    .build();

            return getUserTable().scan(scanRequest)
                    .items()
                    .stream()
                    .findFirst();
        } catch (Exception e) {
            log.error("Error finding user by username: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<User> findByProviderAndProviderId(String provider, String providerId) {
        try {
            Map<String, AttributeValue> expressionValues = new HashMap<>();
            expressionValues.put(":provider", AttributeValue.builder().s(provider).build());
            expressionValues.put(":providerId", AttributeValue.builder().s(providerId).build());

            ScanEnhancedRequest scanRequest = ScanEnhancedRequest.builder()
                    .filterExpression(software.amazon.awssdk.enhanced.dynamodb.Expression.builder()
                            .expression("provider = :provider AND providerId = :providerId")
                            .expressionValues(expressionValues)
                            .build())
                    .build();

            return getUserTable().scan(scanRequest)
                    .items()
                    .stream()
                    .findFirst();
        } catch (Exception e) {
            log.error("Error finding user by provider: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public boolean existsByEmail(String email) {
        return findByEmail(email).isPresent();
    }

    public boolean existsByUsername(String username) {
        return findByUsername(username).isPresent();
    }

    public void deleteById(String userId) {
        try {
            Key key = Key.builder().partitionValue(userId).build();
            getUserTable().deleteItem(key);
            log.info("User deleted successfully: {}", userId);
        } catch (Exception e) {
            log.error("Error deleting user: {}", e.getMessage());
            throw new RuntimeException("Failed to delete user", e);
        }
    }
}
