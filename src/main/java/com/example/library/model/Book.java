package com.example.library.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Size(max = 100)
    private String title;

    @NotBlank(message = "Author is required")
    @Size(max = 100)
    private String author;

    @NotBlank(message = "ISBN is required")
    @Size(max = 20)
    private String isbn;

    @NotNull(message = "Publication year is required")
    private Integer publicationYear;

    @NotBlank(message = "Description is required")
    private String description;

    // Getters and Setters (or use Lombok @Data)

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public @NotBlank(message = "Title is required") @Size(max = 100) String getTitle() {
        return title;
    }

    public void setTitle(@NotBlank(message = "Title is required") @Size(max = 100) String title) {
        this.title = title;
    }

    public @NotBlank(message = "Author is required") @Size(max = 100) String getAuthor() {
        return author;
    }

    public void setAuthor(@NotBlank(message = "Author is required") @Size(max = 100) String author) {
        this.author = author;
    }

    public @NotBlank(message = "ISBN is required") @Size(max = 20) String getIsbn() {
        return isbn;
    }

    public void setIsbn(@NotBlank(message = "ISBN is required") @Size(max = 20) String isbn) {
        this.isbn = isbn;
    }

    public @NotNull(message = "Publication year is required") Integer getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(@NotNull(message = "Publication year is required") Integer publicationYear) {
        this.publicationYear = publicationYear;
    }

    public @NotBlank(message = "Description is required") String getDescription() {
        return description;
    }

    public void setDescription(@NotBlank(message = "Description is required") String description) {
        this.description = description;
    }
}

