package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class DocumentManagerTest {

    private DocumentManager documentManager;

    @BeforeEach
    void setUp() {
        documentManager = new DocumentManager();
    }

    @Test
    void testSaveNewDocument() {
        // Given
        DocumentManager.Document document = DocumentManager.Document.builder()
                .title("Test Title")
                .content("Test Content")
                .author(DocumentManager.Author.builder().id("author1").name("Author Name").build())
                .build();

        // When
        DocumentManager.Document savedDocument = documentManager.save(document);

        // Then
        assertNotNull(savedDocument.getId());
        assertNotNull(savedDocument.getCreated());
        assertEquals("Test Title", savedDocument.getTitle());
        assertEquals("Test Content", savedDocument.getContent());
        assertEquals("author1", savedDocument.getAuthor().getId());
    }

    @Test
    void testSaveUpdateDocument() {
        // Given
        DocumentManager.Document original = DocumentManager.Document.builder()
                .id("doc1")
                .title("Original Title")
                .content("Original Content")
                .author(DocumentManager.Author.builder().id("author1").name("Author Name").build())
                .created(Instant.now())
                .build();
        documentManager.save(original);

        DocumentManager.Document updated = DocumentManager.Document.builder()
                .id("doc1")
                .title("Updated Title")
                .content("Updated Content")
                .author(DocumentManager.Author.builder().id("author1").name("Author Name").build())
                .build();

        // When
        DocumentManager.Document savedDocument = documentManager.save(updated);

        // Then
        assertEquals("doc1", savedDocument.getId());
        assertEquals("Updated Title", savedDocument.getTitle());
        assertEquals("Updated Content", savedDocument.getContent());
        assertEquals(original.getCreated(), savedDocument.getCreated());
    }

    @Test
    void testFindById() {
        // Given
        DocumentManager.Document document = DocumentManager.Document.builder()
                .id("doc1")
                .title("Test Title")
                .content("Test Content")
                .author(DocumentManager.Author.builder().id("author1").name("Author Name").build())
                .created(Instant.now())
                .build();
        documentManager.save(document);

        // When
        Optional<DocumentManager.Document> foundDocument = documentManager.findById("doc1");

        // Then
        assertTrue(foundDocument.isPresent());
        assertEquals("Test Title", foundDocument.get().getTitle());
    }

    @Test
    void testFindByIdNotFound() {
        // When
        Optional<DocumentManager.Document> foundDocument = documentManager.findById("nonexistent");

        // Then
        assertTrue(foundDocument.isEmpty());
    }

    @Test
    void testSearchByTitlePrefix() {
        // Given
        documentManager.save(DocumentManager.Document.builder()
                .title("Title One")
                .content("Content One")
                .author(DocumentManager.Author.builder().id("author1").name("Author One").build())
                .build());
        documentManager.save(DocumentManager.Document.builder()
                .title("Title Two")
                .content("Content Two")
                .author(DocumentManager.Author.builder().id("author2").name("Author Two").build())
                .build());

        DocumentManager.SearchRequest request = DocumentManager.SearchRequest.builder()
                .titlePrefixes(List.of("Title O"))
                .build();

        // When
        List<DocumentManager.Document> results = documentManager.search(request);

        // Then
        assertEquals(1, results.size());
        assertEquals("Title One", results.get(0).getTitle());
    }

    @Test
    void testSearchByDateRange() {
        // Given
        Instant now = Instant.now();
        Instant oneHourAgo = now.minusSeconds(3600); // 1 hour ago
        Instant oneHourLater = now.plusSeconds(3600); // 1 hour later

        // Save documents
        documentManager.save(DocumentManager.Document.builder()
                .title("Document A")
                .created(oneHourAgo)
                .build());
        documentManager.save(DocumentManager.Document.builder()
                .title("Document B")
                .created(oneHourLater)
                .build());

        // Print debug information
        System.out.println("One hour ago: " + oneHourAgo);
        System.out.println("Now: " + now);
        System.out.println("One hour later: " + oneHourLater);

        DocumentManager.SearchRequest request = DocumentManager.SearchRequest.builder()
                .createdFrom(oneHourAgo.minusSeconds(1800)) // 30 minutes before oneHourAgo
                .createdTo(now.plusSeconds(1800))          // 30 minutes ahead of now
                .build();

        // When
        List<DocumentManager.Document> results = documentManager.search(request);

        // Print search results
        results.forEach(doc -> System.out.println("Found document: " + doc.getTitle() + ", created: " + doc.getCreated()));

        // Then
        assertEquals(1, results.size());
        assertEquals("Document A", results.get(0).getTitle());
    }


    @Test
    void testSearchByMultipleCriteria() {
        // Given
        documentManager.save(DocumentManager.Document.builder()
                .title("Title A")
                .content("Content A")
                .author(DocumentManager.Author.builder().id("author1").name("Author One").build())
                .build());
        documentManager.save(DocumentManager.Document.builder()
                .title("Title B")
                .content("Content B")
                .author(DocumentManager.Author.builder().id("author2").name("Author Two").build())
                .build());

        DocumentManager.SearchRequest request = DocumentManager.SearchRequest.builder()
                .titlePrefixes(List.of("Title"))
                .authorIds(List.of("author1"))
                .build();

        // When
        List<DocumentManager.Document> results = documentManager.search(request);

        // Then
        assertEquals(1, results.size());
        assertEquals("Title A", results.get(0).getTitle());
    }
}
