+++
title= "Distributed Search System and TF-IDF Algorithm"
date= 2024-07-01
author= "Me"
showToc= true
TocOpen= false
draft= false
hidemeta= false
comments= false
disableShare= false
disableHLJS= false
hideSummary= false
searchHidden= true
ShowReadingTime= true
ShowBreadCrumbs= true
ShowPostNavLinks= true
ShowWordCount= true
ShowRssButtonInSectionTermList= true
UseHugoToc= true
bookFlatSection= true
weight= 1
+++

# Distributed Search System and TF-IDF Algorithm

## Overview

In this documentation, we describe the development of a distributed search system that allows users to query a large set of documents and receive a ranked list of the most relevant results based on their search terms. The system leverages the **Term Frequency-Inverse Document Frequency (TF-IDF)** algorithm for scoring and ranking documents.

## Problem Definition

Given a large repository of documents such as books, articles, or websites, the system should:
- Accept a search query from the user in real-time.
- Rank documents by relevance to the search query.
- Present a list of documents sorted by relevance.

## Naive Approach

### Word Count Based Scoring
The initial approach to ranking documents is to count the number of times each search term appears in a document. The total score for a document is the sum of the occurrences of the search terms:
- **Search Query**: `"very fast cars"`
- **Document Score**: The sum of occurrences of each word in the document.

#### Example
If "very" appears 20 times, "fast" 10 times, and "cars" 30 times in a document, the score is `60`.

### Issues
This approach is biased towards longer documents, which may contain irrelevant information but still score higher due to their size. For instance, a long book may mention "car" more often than a short article on racing cars, leading to a misleading ranking.

## Improved Approach: Term Frequency (TF)
To counteract the bias towards longer documents, the term frequency is computed by dividing the number of occurrences of the term by the total number of words in the document. This gives the relative importance of the term in the document.

- **Term Frequency (TF)**:  
{{< katex display=true >}}
TF(t, d) = \frac{\text{Occurrences of term } t \text{ in document } d}{\text{Total words in document } d}
{{< /katex >}}

### Example
In a short article, the term "car" may appear frequently, e.g., every 10 words. In a long, irrelevant book, the term might appear only once every 8,000 words, leading to a much lower term frequency.

## Final Approach: TF-IDF Algorithm

### Problem
Some words, like "the," are common across all documents, and relying solely on term frequency can still result in skewed scores.

### TF-IDF Explanation
The **TF-IDF** algorithm addresses this issue by combining **term frequency (TF)** with **inverse document frequency (IDF)**, which adjusts the weight of terms based on how commonly they appear across all documents.

- **Inverse Document Frequency (IDF)**:  
  Measures how unique a term is across the entire document set. It is calculated as:
  {{< katex display=true >}}
  IDF(t, D) = \log\left(\frac{\text{Total number of documents}}{\text{Number of documents containing term } t}\right)
  {{< /katex >}}
  Terms that appear frequently across all documents, like "the," get a lower weight.

### TF-IDF Formula
The TF-IDF score for a term is calculated as:
{{< katex display=true >}}
\text{TF-IDF}(t, d, D) = TF(t, d) \times IDF(t, D)
{{< /katex >}}
The total score for a document is the sum of the TF-IDF scores for all search terms.

### Example
For a search query `"fast cars"`, words like "fast" and "cars" will be given higher weights if they appear in fewer documents. Common terms like "the" will have little to no effect on the document score.

## System Architecture

The distributed search system follows a distributed, parallel processing model:

1. **Document Storage**: All documents are stored in a shared location accessible by all nodes in the cluster. This could be a distributed file system or replicated data across nodes.
2. **Leader Election**: A leader node is elected to coordinate the search operation.
3. **Search Request Handling**: 
    - The user inputs a search query through a web interface.
    - The query is sent to the front-end server and forwarded to the search coordinator.
4. **Parallelized Search**: The coordinator distributes search tasks to worker nodes in the cluster, each responsible for calculating the TF-IDF scores for a subset of documents.
5. **Result Aggregation**: Once the workers complete their tasks, the coordinator aggregates the results, ranks the documents by relevance, and sends the sorted list back to the front-end server.
6. **User Response**: The front-end server formats the results and displays them to the user.

## Considerations

### Dataset Size
The TF-IDF algorithm is more effective with a large set of documents. With a small dataset, the inverse document frequency values might be zero or close to zero, limiting the algorithm's ability to differentiate document relevance.

### Parallelization
The TF-IDF algorithm is highly parallelizable, making it suitable for distributed systems. Each worker node can compute TF-IDF scores for its assigned documents independently, enabling efficient processing of large datasets.

## Scalable Distributed TF-IDF System

### Data Partitioning Strategy

The TF-IDF algorithm consists of two major dimensions:
- **Documents**: The collection of documents over which the search terms will be applied.
- **Search Terms**: The terms for which frequency and inverse document frequency are calculated.

#### Partitioning Options
We can choose to partition the problem along one of these dimensions:
1. **Partition by Terms**: Each node processes a subset of search terms, while all nodes share the same set of documents.
2. **Partition by Documents**: Each node processes a subset of documents, while all nodes share the same set of search terms.

#### Decision
- **Partition by Documents**: The system is expected to handle an increasing number of documents over time, making this the scalable option. Partitioning by documents allows us to add more machines to handle document growth effectively.

#### Key Considerations
- **Search Terms**: The number of search terms typically remains small (e.g., a sentence or a few sentences) and is not expected to grow significantly.
- **Documents**: As the document collection grows, partitioning by documents ensures horizontal scalability.

### Parallelizing the TF-IDF Algorithm

The TF-IDF algorithm is split into two key components:
1. **Term Frequency (TF)**: The frequency of a search term within a particular document.
2. **Inverse Document Frequency (IDF)**: Measures how common or rare a term is across all documents.

#### Parallel Computation of TF
- **Parallelizable**: The computation of term frequency for a given term within a specific document is independent of other documents, making it parallelizable.
- **Node Task**: Each node is assigned a subset of documents to process. For each document, the node calculates the term frequency for all search terms.

### Serial Computation of IDF
- **Non-Parallelizable**: The calculation of inverse document frequency requires knowledge of all documents in the repository, meaning it cannot be distributed across nodes.
- **Leader Task**: After receiving term frequencies from all nodes, the leader node calculates the IDF for each search term based on the global document set.

#### Parallel Algorithm Execution

1. **Leader Node**: 
   - Splits the documents into equal subsets and assigns them to the worker nodes.
   - Sends a task containing the search terms and the document subset to each node.
   
2. **Worker Nodes**: 
   - Process their assigned documents.
   - Compute the term frequency for each search term within the documents.
   - Send the results back to the leader node.

3. **Leader Node**:
   - Receives the term frequencies from all worker nodes.
   - Computes the global IDF for each search term.
   - Aggregates the results, scores the documents based on TF-IDF values, and sorts them in descending order.
   - Returns the final results to the frontend system.

### Components
1. **Cluster of Worker Nodes**: Each worker node is responsible for calculating term frequencies for its assigned subset of documents.
2. **Frontend Server**: Receives the search query and forwards it to the search coordinator (leader node).
3. **Leader Node**: Acts as the search coordinator, managing task distribution, aggregation, and final scoring.
4. **Document Repository**: A centralized storage system containing all the documents to be processed.
5. **Zookeeper Service Registry**: 
   - Handles leader election and worker node registration.
   - The frontend server uses Zookeeper to find the leader node and send search queries to it.

### Workflow
1. **Leader Election**: Using Zookeeper, a leader is elected to coordinate the search operation.
2. **Task Distribution**: The leader distributes tasks to the worker nodes, ensuring an even partition of the documents.
3. **Document Processing**: Each worker node retrieves its allocated documents from the document repository, computes term frequencies, and sends results to the leader.
4. **Aggregation and Scoring**: The leader aggregates the term frequencies, computes the IDF, scores the documents, and returns the sorted results to the frontend server.

### Communication Flow
- The leader node communicates with the worker nodes via HTTP requests, sending tasks and receiving results.
- The frontend server communicates with the leader node via the Zookeeper service registry to forward search queries and receive results.

### Zookeeper Roles
- **Service Registry**: Keeps track of available worker nodes and their addresses.
- **Leader Election**: Ensures one leader is selected to coordinate the search operation.

## Implementation

**SearchWorker class:**
- Handles incoming requests, processes tasks by parsing documents, and computes term frequencies for the search terms using the TFIDF class.

```java
public class SearchWorker implements OnRequestCallback {
    // Define the endpoint this worker listens to
    private static final String ENDPOINT = "/task";

    @Override
    public byte[] handleRequest(byte[] requestPayload) {
        // Deserialize the request payload into a Task object
        Task task = (Task) SerializationUtils.deserialize(requestPayload);

        // Process the task and create a result
        Result result = createResult(task);

        // Serialize the result object and return it
        return SerializationUtils.serialize(result);
    }

    private Result createResult(Task task) {
        // Retrieve the list of documents to process
        List<String> documents = task.getDocuments();
        System.out.println(String.format("Received %d documents to process", documents.size()));

        // Create a Result object to hold document analysis data
        Result result = new Result();

        // Process each document
        for (String document : documents) {
            // Parse the document into words
            List<String> words = parseWordsFromDocument(document);

            // Create DocumentData by calculating TF for search terms in the document
            DocumentData documentData = TFIDF.createDocumentData(words, task.getSearchTerms());

            // Add the processed document data to the result
            result.addDocumentData(document, documentData);
        }
        return result;
    }

    private List<String> parseWordsFromDocument(String document) {
        try {
            // Read the document from the file system
            FileReader fileReader = new FileReader(document);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            // Collect the lines of the document
            List<String> lines = bufferedReader.lines().collect(Collectors.toList());

            // Extract words from the document lines
            List<String> words = TFIDF.getWordsFromDocument(lines);

            return words;
        } catch (FileNotFoundException e) {
            // If the document is not found, return an empty list
            return Collections.emptyList();
        }
    }

    @Override
    public String getEndpoint() {
        // Return the endpoint this worker is associated with
        return ENDPOINT;
    }
}
```

**TFIDF class:**
- Calculates Term Frequency (TF) and Inverse Document Frequency (IDF) to rank documents based on search terms.
- Provides utilities for splitting text into words, computing scores for documents based on TF-IDF, and sorting documents by relevance.

```java
// TFIDF class for calculating Term Frequency (TF) and Inverse Document Frequency (IDF)
package search;

import model.DocumentData;

import java.util.*;

public class TFIDF {

    // Calculate term frequency (TF) of a term in a document (list of words)
    public static double calculateTermFrequency(List<String> words, String term) {
        long count = 0;

        // Count occurrences of the term in the document
        for (String word : words) {
            if (term.equalsIgnoreCase(word)) {
                count++;
            }
        }

        // Return the frequency as a proportion of the total words
        double termFrequency = (double) count / words.size();
        return termFrequency;
    }

    // Create DocumentData containing TF for each search term
    public static DocumentData createDocumentData(List<String> words, List<String> terms) {
        DocumentData documentData = new DocumentData();

        // For each search term, calculate its TF in the document
        for (String term : terms) {
            double termFreq = TFIDF.calculateTermFrequency(words, term.toLowerCase());
            documentData.putTermFrequency(term, termFreq);
        }
        return documentData;
    }

    // Calculate document scores based on search terms and TF-IDF values
    public static Map<Double, List<String>> getDocumentsScores(List<String> terms,
                                                               Map<String, DocumentData> documentResults) {
        // TreeMap stores document scores in sorted order
        TreeMap<Double, List<String>> scoreToDoc = new TreeMap<>();

        // Calculate IDF for each search term
        Map<String, Double> termToInverseDocumentFrequency = getTermToInverseDocumentFrequencyMap(terms, documentResults);

        // Calculate a score for each document based on TF-IDF
        for (String document : documentResults.keySet()) {
            DocumentData documentData = documentResults.get(document);

            // Calculate the score using TF-IDF
            double score = calculateDocumentScore(terms, documentData, termToInverseDocumentFrequency);

            // Add the document and its score to the TreeMap
            addDocumentScoreToTreeMap(scoreToDoc, score, document);
        }
        return scoreToDoc.descendingMap(); // Return the scores in descending order
    }

    // Helper method to add a document's score to the TreeMap
    private static void addDocumentScoreToTreeMap(TreeMap<Double, List<String>> scoreToDoc, double score, String document) {
        // Get the list of documents with the same score
        List<String> booksWithCurrentScore = scoreToDoc.get(score);

        if (booksWithCurrentScore == null) {
            booksWithCurrentScore = new ArrayList<>();
        }

        // Add the document to the list of documents with the current score
        booksWithCurrentScore.add(document);
        scoreToDoc.put(score, booksWithCurrentScore);
    }

    // Calculate the score of a document based on TF-IDF of the search terms
    private static double calculateDocumentScore(List<String> terms,
                                                 DocumentData documentData,
                                                 Map<String, Double> termToInverseDocumentFrequency) {
        double score = 0;

        // Sum TF-IDF scores for each term in the document
        for (String term : terms) {
            double termFrequency = documentData.getFrequency(term);
            double inverseTermFrequency = termToInverseDocumentFrequency.get(term);
            score += termFrequency * inverseTermFrequency;
        }
        return score;
    }

    // Calculate IDF for a specific term across all documents
    private static double getInverseDocumentFrequency(String term, Map<String, DocumentData> documentResults) {
        double n = 0;

        // Count how many documents contain the term
        for (String document : documentResults.keySet()) {
            DocumentData documentData = documentResults.get(document);
            double termFrequency = documentData.getFrequency(term);
            if (termFrequency > 0.0) {
                n++;
            }
        }

        // Calculate IDF using log formula, handling case when term is not present
        return n == 0 ? 0 : Math.log10(documentResults.size() / n);
    }

    // Create a map of terms to their IDF values
    private static Map<String, Double> getTermToInverseDocumentFrequencyMap(List<String> terms,
                                                                            Map<String, DocumentData> documentResults) {
        Map<String, Double> termToIDF = new HashMap<>();

        // Calculate IDF for each search term
        for (String term : terms) {
            double idf = getInverseDocumentFrequency(term, documentResults);
            termToIDF.put(term, idf);
        }
        return termToIDF;
    }

    // Extract words from a list of document lines
    public static List<String> getWordsFromDocument(List<String> lines) {
        List<String> words = new ArrayList<>();

        // Split each line into words and add to the list
        for (String line : lines) {
            words.addAll(getWordsFromLine(line));
        }
        return words;
    }

    // Split a single line of text into words using regex
    public static List<String> getWordsFromLine(String line) {
        // Split on common delimiters like punctuation and whitespace
        return Arrays.asList(line.split("(\\.)+|(,)+|( )+|(-)+|(\\?)+|(!)+|(;)+|(:)+|(/d)+|(/n)+"));
    }
}
```

**DocumentData class:**

- **termToFrequency**: A Map that holds each term and its corresponding frequency within a document.
- **putTermFrequency(String term, double frequency)**: This method stores the frequency of a given term in the termToFrequency map.
- **getFrequency(String term)**: This method retrieves the frequency of a specific term from the termToFrequency map, allowing the system to access term frequency data for further calculations (e.g., TF-IDF score).

```java
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class DocumentData implements Serializable {
    
    // Map to store term frequencies for each term in the document
    private Map<String, Double> termToFrequency = new HashMap<>();

    // Adds a term-frequency pair to the map
    public void putTermFrequency(String term, double frequency) {
        termToFrequency.put(term, frequency);  // Store the frequency of a given term
    }

    // Retrieves the frequency of a term from the map
    public double getFrequency(String term) {
        return termToFrequency.get(term);  // Return the frequency of the term
    }
}
```

**Task class:**

- **searchTerms**: A list of search terms for which the task will compute term frequencies.
- **documents**: A list of documents that the task will process.
- **Task(List<String> searchTerms, List<String> documents)**: Constructor that initializes the search terms and documents.
- **getSearchTerms()**: Getter method to retrieve the search terms.
- **getDocuments()**: Getter method to retrieve the documents.

```java
import java.io.Serializable;
import java.util.List;

public class Task implements Serializable {
    
    // A list of search terms for which the task will compute term frequencies
    private final List<String> searchTerms;
    
    // A list of documents that the task will process
    private final List<String> documents;

    // Constructor that initializes the search terms and documents
    public Task(List<String> searchTerms, List<String> documents) {
        this.searchTerms = searchTerms;  // Set the list of search terms
        this.documents = documents;  // Set the list of documents
    }

    // Getter method to retrieve the search terms
    public List<String> getSearchTerms() {
        return searchTerms;  // Return the list of search terms
    }

    // Getter method to retrieve the documents
    public List<String> getDocuments() {
        return documents;  // Return the list of documents
    }
}
```

**SearchCoordinator class:**

- **documents**: A list of documents to be searched.
- **workersServiceRegistry**: A registry to manage worker nodes.
- **client**: A web client used to send tasks to workers.
- **SearchCoordinator(ServiceRegistry workersServiceRegistry, WebClient client)**: Constructor to initialize SearchCoordinator with service registry and web client.
- **handleRequest(byte[] requestPayload)**: Handles incoming search requests and returns a response.
- **createResponse(SearchModel.Request searchRequest)**: Creates a response to a search request by sending tasks to workers and aggregating results.
- **aggregateResults(List<Result> results, List<String> terms)**: Aggregates results from workers and calculates document scores.
- **sortDocumentsByScore(Map<Double, List<String>> scoreToDocuments)**: Sorts documents by their score.

```java
package search;

import cluster.management.ServiceRegistry;
import com.google.protobuf.InvalidProtocolBufferException;
import model.DocumentData;
import model.Result;
import model.SerializationUtils;
import model.Task;
import model.proto.SearchModel;
import networking.OnRequestCallback;
import networking.WebClient;
import org.apache.zookeeper.KeeperException;

import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Search Coordinator class manages the distribution of search tasks across worker nodes
 * and coordinates the collection and aggregation of search results.
 */
public class SearchCoordinator implements OnRequestCallback {
    private static final String ENDPOINT = "/search";  // Endpoint for handling search requests
    private static final String BOOKS_DIRECTORY = "./resources/books/";  // Directory where documents are stored
    private final ServiceRegistry workersServiceRegistry;  // Registry to manage worker nodes
    private final WebClient client;  // Client for communication with worker nodes
    private final List<String> documents;  // List of documents to be searched

    /**
     * Constructor to initialize SearchCoordinator with service registry and web client.
     *
     * @param workersServiceRegistry The service registry for worker nodes.
     * @param client The web client used to send tasks to workers.
     */
    public SearchCoordinator(ServiceRegistry workersServiceRegistry, WebClient client) {
        this.workersServiceRegistry = workersServiceRegistry;  // Set service registry
        this.client = client;  // Set web client
        this.documents = readDocumentsList();  // Load the list of documents
    }

    /**
     * Handles incoming search requests and returns a response.
     *
     * @param requestPayload The request payload in bytes.
     * @return The response in bytes.
     */
    public byte[] handleRequest(byte[] requestPayload) {
        try {
            // Parse the request payload to a SearchModel.Request object
            SearchModel.Request request = SearchModel.Request.parseFrom(requestPayload);
            // Create a response based on the request
            SearchModel.Response response = createResponse(request);

            // Return the response as a byte array
            return response.toByteArray();
        } catch (InvalidProtocolBufferException | KeeperException | InterruptedException e) {
            e.printStackTrace();  // Handle exceptions
            return SearchModel.Response.getDefaultInstance().toByteArray();  // Return default response in case of error
        }
    }

    /**
     * Returns the endpoint for search requests.
     *
     * @return The endpoint string.
     */
    @Override
    public String getEndpoint() {
        return ENDPOINT;  // Return the endpoint string
    }

    /**
     * Creates a response to a search request by sending tasks to workers and aggregating results.
     *
     * @param searchRequest The search request to process.
     * @return The search response.
     * @throws KeeperException If there's an issue with ZooKeeper.
     * @throws InterruptedException If the thread is interrupted.
     */
    private SearchModel.Response createResponse(SearchModel.Request searchRequest) throws KeeperException, InterruptedException {
        SearchModel.Response.Builder searchResponse = SearchModel.Response.newBuilder();

        // Print received search query
        System.out.println("Received search query: " + searchRequest.getSearchQuery());

        // Extract search terms from the query
        List<String> searchTerms = TFIDF.getWordsFromLine(searchRequest.getSearchQuery());

        // Get the list of worker nodes
        List<String> workers = workersServiceRegistry.getAllServiceAddresses();

        // If no workers are available, return an empty response
        if (workers.isEmpty()) {
            System.out.println("No search workers currently available");
            return searchResponse.build();
        }

        // Create tasks for each worker
        List<Task> tasks = createTasks(workers.size(), searchTerms);
        // Send tasks to workers and collect results
        List<Result> results = sendTasksToWorkers(workers, tasks);

        // Aggregate and sort the results
        List<SearchModel.Response.DocumentStats> sortedDocuments = aggregateResults(results, searchTerms);
        // Add the sorted documents to the response
        searchResponse.addAllRelevantDocuments(sortedDocuments);

        return searchResponse.build();  // Build and return the response
    }

    /**
     * Aggregates results from workers and calculates document scores.
     *
     * @param results The list of results from workers.
     * @param terms The search terms.
     * @return A list of sorted document statistics.
     */
    private List<SearchModel.Response.DocumentStats> aggregateResults(List<Result> results, List<String> terms) {
        Map<String, DocumentData> allDocumentsResults = new HashMap<>();

        // Merge results from all workers
        for (Result result : results) {
            allDocumentsResults.putAll(result.getDocumentToDocumentData());
        }

        // Print message about score calculation
        System.out.println("Calculating score for all the documents");
        // Calculate scores for documents
        Map<Double, List<String>> scoreToDocuments = TFIDF.getDocumentsScores(terms, allDocumentsResults);

        // Sort documents by their score
        return sortDocumentsByScore(scoreToDocuments);
    }

    /**
     * Sorts documents by their score.
     *
     * @param scoreToDocuments A map of scores to document names.
     * @return A list of sorted document statistics.
     */
    private List<SearchModel.Response.DocumentStats> sortDocumentsByScore(Map<Double, List<String>> scoreToDocuments) {
        List<SearchModel.Response.DocumentStats> sortedDocumentsStatsList = new ArrayList<>();

        // Build document statistics from sorted scores
        for (Map.Entry<Double, List<String>> docScorePair : scoreToDocuments.entrySet()) {
            double score = docScorePair.getKey();

            for (String document : docScorePair.getValue()) {
                File documentPath = new File(document);

                // Create a DocumentStats object
                SearchModel.Response.DocumentStats documentStats = SearchModel.Response.DocumentStats.newBuilder()
                        .setScore(score)
                        .setDocumentName(documentPath.getName())
                        .setDocumentSize(documentPath.length())
                        .build();

                sortedDocumentsStatsList.add(documentStats);  // Add to list
            }
        }

        return sortedDocumentsStatsList;  // Return the sorted list
    }

    /**
     * Sends tasks to worker nodes and collects their results.
     *
     * @param workers The list of worker addresses.
     * @param tasks The list of tasks to send.
     * @return A list of results from the workers.
     */
    private List<Result> sendTasksToWorkers(List<String> workers, List<Task> tasks) {
        CompletableFuture<Result>[] futures = new CompletableFuture[workers.size()];
        for (int i = 0; i < workers.size(); i++) {
            String worker = workers.get(i);
            Task task = tasks.get(i);
            byte[] payload = SerializationUtils.serialize(task);

            // Send the task to the worker and store the future
            futures[i] = client.sendTask(worker, payload);
        }

        List<Result> results = new ArrayList<>();
        for (CompletableFuture<Result> future : futures) {
            try {
                Result result = future.get();  // Get the result from the future
                results.add(result);  // Add result to list
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();  // Handle exceptions
            }
        }
        // Print received results
        System.out.println(String.format("Received %d/%d results", results.size(), tasks.size()));
        return results;  // Return the list of results
    }

    /**
     * Creates a list of tasks to be distributed among workers.
     *
     * @param numberOfWorkers The number of worker nodes.
     * @param searchTerms The list of search terms.
     * @return A list of tasks for the workers.
     */
    public List<Task> createTasks(int numberOfWorkers, List<String> searchTerms) {
        List<List<String>> workersDocuments = splitDocumentList(numberOfWorkers, documents);

        List<Task> tasks = new ArrayList<>();

        // Create a task for each worker
        for (List<String> documentsForWorker : workersDocuments) {
            Task task = new Task(searchTerms, documentsForWorker);
            tasks.add(task);
        }

        return tasks;  // Return the list of tasks
    }

    /**
     * Splits the list of documents into sublists for each worker.
     *
     * @param numberOfWorkers The number of workers.
     * @param documents The list of documents to split.
     * @return A list of document sublists for each worker.
     */
    private static List<List<String>> splitDocumentList(int numberOfWorkers, List<String> documents) {
        int numberOfDocumentsPerWorker = (documents.size() + numberOfWorkers - 1) / numberOfWorkers;

        List<List<String>> workersDocuments = new ArrayList<>();

        // Split the documents into chunks for each worker
        for (int i = 0; i < numberOfWorkers; i++) {
            int firstDocumentIndex = i * numberOfDocumentsPerWorker;
            int lastDocumentIndexExclusive = Math.min(firstDocumentIndex + numberOfDocumentsPerWorker, documents.size());

            if (firstDocumentIndex >= lastDocumentIndexExclusive) {
                break;  // Exit loop if no more documents
            }
            List<String> currentWorkerDocuments = new ArrayList<>(documents.subList(firstDocumentIndex, lastDocumentIndexExclusive));

            workersDocuments.add(currentWorkerDocuments);  // Add chunk to list
        }
        return workersDocuments;  // Return the list of document sublists
    }

    /**
     * Reads the list of documents from the directory.
     *
     * @return A list of document file paths.
     */
    private static List<String> readDocumentsList() {
        File documentsDirectory = new File(BOOKS_DIRECTORY);
        return Arrays.asList(documentsDirectory.list())  // Get the list of file names in the directory
                .stream()
                .map(documentName -> BOOKS_DIRECTORY + "/" + documentName)  // Construct full file paths
                .collect(Collectors.toList());  // Collect paths into a list
    }
}
```

