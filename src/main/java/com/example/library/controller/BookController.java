package com.example.library.controller;




import com.example.library.exception.AIServiceException;
import com.example.library.exception.ResourceNotFoundException;
import com.example.library.model.Book;
import com.example.library.model.AIResponse;
import com.example.library.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.Duration;
import reactor.util.retry.Retry;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatCompletion;
import com.openai.models.ChatCompletionCreateParams;
import com.openai.models.ChatModel;


@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/books")
@Validated
public class BookController {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Value("${openai.api.url}")
    private String aiApiUrl;

    @Value("${openai.api.key}")
    private String aiApiKey;

    // Create a New Book
    @PostMapping
    public ResponseEntity<Book> createBook(@Valid @RequestBody Book book) {
        Book savedBook = bookRepository.save(book);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBook);
    }

    // Retrieve All Books
    @GetMapping
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    // Retrieve a Single Book by ID
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id " + id));
        return ResponseEntity.ok(book);
    }

    // Update an Existing Book
    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable Long id, @Valid @RequestBody Book bookDetails) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id " + id));

        book.setTitle(bookDetails.getTitle());
        book.setAuthor(bookDetails.getAuthor());
        book.setIsbn(bookDetails.getIsbn());
        book.setPublicationYear(bookDetails.getPublicationYear());
        book.setDescription(bookDetails.getDescription());

        Book updatedBook = bookRepository.save(book);
        return ResponseEntity.ok(updatedBook);
    }

    // Delete a Book
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id " + id));
        bookRepository.delete(book);
        return ResponseEntity.noContent().build();
    }

    // Search for Books
    @GetMapping("/search")
    public List<Book> searchBooks(@RequestParam(required = false) String title,
                                  @RequestParam(required = false) String author) {
        if (title != null && author != null) {
            return bookRepository.findByTitleContainingIgnoreCaseAndAuthorContainingIgnoreCase(title, author);
        } else if (title != null) {
            return bookRepository.findByTitleContainingIgnoreCase(title);
        } else if (author != null) {
            return bookRepository.findByAuthorContainingIgnoreCase(author);
        } else {
            return bookRepository.findAll();
        }
    }
    
    // AI-Powered Book Insights
    @GetMapping("/{id}/ai-insights")
    public ResponseEntity<?> getAiInsights(@PathVariable Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id " + id));

        // Build a prompt using the book's details
        String prompt = "Provide a short, engaging summary for the book titled '" + book.getTitle() +
                "' by " + book.getAuthor() + ". Description: " + book.getDescription();

        try {
            // Prepare the request payload for the AI API
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "gpt-4o-mini"); // specify the model
            requestBody.put("messages", new Object[]{
                    Map.of("role", "system", "content", "You are an expert in book summaries."),
                    Map.of("role", "user", "content", prompt)
            });
            requestBody.put("max_tokens", 200);           // adjust as needed
            requestBody.put("temperature", 0.8);          // optional: adjust creativity

            System.out.println(requestBody);

            // Call external AI API
            AIResponse aiResponse = webClientBuilder.build()
                    .post()
                    .uri(aiApiUrl)
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + aiApiKey)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(AIResponse.class)
                    .retryWhen(Retry.backoff(5, Duration.ofSeconds(3))) // Retries 3 times with increasing delay
                    .doOnNext(System.out::println)
                    .doOnError(error -> System.out.println("Error: " + error.getMessage()))
                    .block();

            Map<String, Object> response = new HashMap<>();
            response.put("book", book);
            response.put("aiInsights", aiResponse.getInsight());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println(e.getMessage());
                throw new AIServiceException("Failed to get AI insights: " + e.getMessage());
        }
    }
}

