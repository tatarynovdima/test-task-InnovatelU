# Document Management System - Implemented methods


### 1. `save` Method
- **Functionality**: Saves a new document or updates an existing one.
- **ID Generation**: 
  - If the document does not have an ID, a unique ID is generated using `UUID.randomUUID().toString()`.
- **Preservation of "created" Field**: 
  - If the document already exists, its `created` timestamp is preserved.
  - If it's a new document, the current timestamp (`Instant.now()`) is assigned to the `created` field.
- **Storage**: Documents are stored in an in-memory `ConcurrentHashMap`, where the key is the document's ID.
- **Return**: The saved document is returned after the operation.

### 2. `search` Method
- **Functionality**: Filters and returns a list of documents based on the search criteria provided in the `SearchRequest` object.
- **Filtering Criteria**:
  - **Title Prefixes**: Checks if the document's title starts with any of the given prefixes.
  - **Content Substrings**: Filters documents whose content contains any of the provided substrings.
  - **Author IDs**: Filters documents by matching the author's ID with the provided list.
  - **Date Range**: Filters documents by their `created` date, ensuring it falls within the specified range (`createdFrom` and `createdTo`).
- **Implementation**: Each criterion is applied as a separate filter using Java Streams API (`stream()`), and the documents are collected into a list.

### 3. `findById` Method
- **Functionality**: Searches for a document by its ID.
- **Return**: Returns an `Optional<Document>`, which is either empty if no document is found, or contains the document if it exists in storage.

### 4. Helper Methods
You implemented several helper methods to check if a document matches specific criteria:
- **`matchesPrefix`**: Checks if the document's title starts with any of the given prefixes.
- **`matchesSubstring`**: Checks if the document's content contains any of the given substrings.
- **`matchesAuthor`**: Checks if the document's author matches any of the provided author IDs.
- **`matchesDateRange`**: Ensures that the document's `created` date falls within the specified range.

### 5. Data Classes
The following data classes are used in the system:
- **`Document`**: Represents the document with fields such as ID, title, content, author, and created timestamp.
- **`SearchRequest`**: Contains the search criteria used to filter documents.
- **`Author`**: Represents the author of a document.
  

# Document Management System - Implemented test cases

### 1. `testSaveNewDocument`
- **Objective**: Tests saving a new document.
- **Given**: A new document with title, content, and author.
- **When**: The `save` method is called.
- **Then**: The test checks that the document is saved with:
  - A non-null ID.
  - A non-null creation timestamp.
  - Correct title, content, and author.

### 2. `testSaveUpdateDocument`
- **Objective**: Tests updating an existing document while preserving its created timestamp.
- **Given**: A document with a predefined ID (`doc1`), title, content, and author is saved first.
- **When**: The document is updated (with a new title and content) and saved again.
- **Then**: The test ensures that:
  - The document's ID remains the same.
  - The title and content are updated.
  - The created timestamp stays unchanged (it preserves the original creation time).

### 3. `testFindById`
- **Objective**: Tests finding a document by its ID.
- **Given**: A document is saved with a known ID (`doc1`).
- **When**: The `findById` method is called with the ID `doc1`.
- **Then**: The test ensures that:
  - The document is found.
  - The title matches the expected value.

### 4. `testFindByIdNotFound`
- **Objective**: Tests the scenario when a document is not found by its ID.
- **Given**: No document is saved with the ID `nonexistent`.
- **When**: The `findById` method is called with the ID `nonexistent`.
- **Then**: The test checks that an empty `Optional` is returned, indicating the document does not exist.

### 5. `testSearchByTitlePrefix`
- **Objective**: Tests searching for documents by title prefix.
- **Given**: Two documents with titles "Title One" and "Title Two" are saved.
- **When**: The `search` method is called with a request for titles that start with "Title O".
- **Then**: The test ensures that only one document ("Title One") matches the prefix filter.

### 6. `testSearchByDateRange`
- **Objective**: Tests searching for documents within a specific date range.
- **Given**: Two documents are saved, one created one hour ago and one created one hour later.
- **When**: The `search` method is called with a date range that includes the document created one hour ago but excludes the one created one hour later.
- **Then**: The test verifies that only the document created one hour ago is returned, ensuring the date range filtering works correctly.

### 7. `testSearchByMultipleCriteria`
- **Objective**: Tests searching for documents using multiple criteria: title prefix and author ID.
- **Given**: Two documents with different titles and authors are saved.
- **When**: The `search` method is called with a request for documents with titles starting with "Title" and authored by "author1".
- **Then**: The test ensures that only the document with title "Title A" and author "author1" is returned.

