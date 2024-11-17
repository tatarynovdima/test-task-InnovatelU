Implemented methods

1. save Method:
**Functionality: This method either saves a new document or updates an existing one.
ID Generation: If the document does not have an ID, a unique ID is generated using UUID.randomUUID().toString().
Preservation of "created" field: If the document already exists, its created field (timestamp) is preserved. If it's a new document, the current timestamp (Instant.now()) is assigned to the created field.
Storage: Documents are stored in an in-memory ConcurrentHashMap where the key is the document's ID.
Return: The saved document is returned after the operation.
2. search Method:
Functionality: This method filters and returns a list of documents based on the search criteria in the SearchRequest object.
Filtering Criteria:
Title Prefixes: Checks if the document's title starts with any of the given prefixes.
Content Substrings: Filters documents whose content contains any of the provided substrings.
Author IDs: Filters documents by matching the author's ID with the provided list.
Date Range: Filters documents by their created date, ensuring that it falls within the specified date range (createdFrom and createdTo).
Implementation: Each criterion is applied as a separate filter using the stream() API, and the documents are collected into a list.
3. findById Method:
Functionality: This method searches for a document by its ID.
Return: The method returns an Optional<Document>, which is either empty if no document is found, or contains the document if it exists in storage.
4. Helper Methods:
Filtering Logic: You implemented several helper methods to check if a document matches specific criteria:
matchesPrefix: Checks if the document's title starts with any of the given prefixes.
matchesSubstring: Checks if the document's content contains any of the given substrings.
matchesAuthor: Checks if the document's author matches any of the given author IDs.
matchesDateRange: Ensures that the document's created date falls within the specified range.
5. Data Classes (SearchRequest, Document, Author):
The Document, SearchRequest, and Author classes are implemented as simple POJOs with Lombok annotations (@Data, @Builder) for automatic generation of getters, setters, and builders, making the code more concise and readable.
Summary of the Approach:
You used an in-memory ConcurrentHashMap for storing documents, ensuring efficient lookups and updates.
The save method handles both the creation of new documents and updates to existing ones, while preserving the original creation timestamp.
The search method allows for flexible filtering of documents based on multiple criteria (title, content, author, date).
The findById method ensures easy retrieval of documents by their ID.


Implemented test

1. testSaveNewDocument:
Objective: Tests saving a new document.
Given: A new document with title, content, and author.
When: The save method is called.
Then: The test checks that the document is saved with a non-null ID, non-null creation timestamp, and correct title, content, and author.
2. testSaveUpdateDocument:
Objective: Tests updating an existing document while preserving its created timestamp.
Given: A document with a predefined ID (doc1), title, content, and author is saved first.
When: The document is updated (with a new title and content) and saved again.
Then: The test ensures that the document's ID remains the same, the title and content are updated, and the created timestamp stays unchanged (it preserves the original creation time).
3. testFindById:
Objective: Tests finding a document by its ID.
Given: A document is saved with a known ID (doc1).
When: The findById method is called with the ID doc1.
Then: The test ensures that the document is found and its title matches the expected value.
4. testFindByIdNotFound:
Objective: Tests the scenario when a document is not found by its ID.
Given: No document is saved with the ID nonexistent.
When: The findById method is called with the ID nonexistent.
Then: The test checks that an empty Optional is returned, indicating the document does not exist.
5. testSearchByTitlePrefix:
Objective: Tests searching for documents by title prefix.
Given: Two documents with titles "Title One" and "Title Two" are saved.
When: The search method is called with a request for titles that start with "Title O".
Then: The test ensures that only one document ("Title One") matches the prefix filter.
6. testSearchByDateRange:
Objective: Tests searching for documents within a specific date range.
Given: Two documents are saved, one created one hour ago and one created one hour later.
When: The search method is called with a date range that includes the document created one hour ago but excludes the one created one hour later.
Then: The test verifies that only the document created one hour ago is returned, ensuring the date range filtering works correctly.
7. testSearchByMultipleCriteria:
Objective: Tests searching for documents using multiple criteria: title prefix and author ID.
Given: Two documents with different titles and authors are saved.
When: The search method is called with a request for documents with titles starting with "Title" and authored by "author1".
Then: The test ensures that only the document with title "Title A" and author "author1" is returned.
