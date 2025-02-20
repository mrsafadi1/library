package com.example.library;

import com.example.library.model.Book;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class BookControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreateAndGetBook() throws Exception {
        Book book = new Book();
        book.setTitle("Spring in Action");
        book.setAuthor("Craig Walls");
        book.setIsbn("9781617294945");
        book.setPublicationYear(2018);
        book.setDescription("A comprehensive guide to Spring Framework.");

        // Create the book
        String json = objectMapper.writeValueAsString(book);
        String response = mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Book createdBook = objectMapper.readValue(response, Book.class);

        // Retrieve the book
        mockMvc.perform(get("/books/" + createdBook.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Spring in Action"));
    }
}

