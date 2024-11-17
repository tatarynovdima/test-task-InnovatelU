package org.example;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * For implement this task focus on clear code, and make this solution as simple readable as possible
 * Don't worry about performance, concurrency, etc
 * You can use in Memory collection for store data
 */
public class DocumentManager {
    private final Map<String, Document> storage = new ConcurrentHashMap<>();

    /**
     * Saves or updates a document in the storage.
     * If the document lacks an ID, a new one is generated.
     * The "created" field is preserved during updates.
     *
     * @param document - document content and author data
     * @return saved document
     */
    public Document save(Document document) {
        // If the document doesn't have an ID, generate a new UUID
        if (document.getId() == null || document.getId().isEmpty()) {
            document.setId(UUID.randomUUID().toString());
        }

        // If the creation timestamp is not set, assign the current time
        if (document.getCreated() == null) {
            document.setCreated(Instant.now());
        }

        // Retrieve the existing document from storage (if it exists)
        Document existing = storage.get(document.getId());

        // If the document already exists, preserve its original creation timestamp
        if (existing != null) {
            document.setCreated(existing.getCreated());
        }

        storage.put(document.getId(), document);
        return document;
    }


    /**
     * Searches for documents based on the given search request criteria.
     *
     * @param request the search request containing various filters
     * @return a list of documents matching the search criteria
     */
    public List<Document> search(SearchRequest request) {
        return storage.values().stream()
                .filter(doc -> matchesPrefix(doc.getTitle(), request.getTitlePrefixes()))
                .filter(doc -> matchesSubstring(doc.getContent(), request.getContainsContents()))
                .filter(doc -> matchesAuthor(doc.getAuthor(), request.getAuthorIds()))
                .filter(doc -> matchesDateRange(doc.getCreated(), request.getCreatedFrom(), request.getCreatedTo()))
                .collect(Collectors.toList());
    }

    /**
     * Finds a document by its ID.
     *
     * @param id the document ID
     * @return an Optional containing the document if found
     */
    public Optional<Document> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    // Helper methods for filtering

    /**
     * Checks if a field matches any of the given prefixes.
     */
    private boolean matchesPrefix(String field, List<String> prefixes) {
        return isNullOrEmpty(prefixes) ||
                (field != null && prefixes.stream().anyMatch(field::startsWith));
    }

    /**
     * Checks if a field contains any of the given substrings.
     */
    private boolean matchesSubstring(String field, List<String> substrings) {
        return isNullOrEmpty(substrings) ||
                (field != null && substrings.stream().anyMatch(field::contains));
    }

    /**
     * Checks if the author's ID matches any of the given author IDs.
     */
    private boolean matchesAuthor(Author author, List<String> authorIds) {
        return isNullOrEmpty(authorIds) ||
                (author != null && authorIds.contains(author.getId()));
    }

    /**
     * Checks if a date falls within the given date range.
     */
    private boolean matchesDateRange(Instant created, Instant from, Instant to) {
        return (from == null || (created != null && !created.isBefore(from))) &&
                (to == null || (created != null && !created.isAfter(to)));
    }

    /**
     * Checks if a collection is null or empty.
     */
    private static boolean isNullOrEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    @Data
    @Builder
    public static class SearchRequest {
        private List<String> titlePrefixes;
        private List<String> containsContents;
        private List<String> authorIds;
        private Instant createdFrom;
        private Instant createdTo;
    }

    @Data
    @Builder
    public static class Document {
        private String id;
        private String title;
        private String content;
        private Author author;
        private Instant created;
    }

    @Data
    @Builder
    public static class Author {
        private String id;
        private String name;
    }
}
