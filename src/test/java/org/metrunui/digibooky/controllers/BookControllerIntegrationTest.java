package org.metrunui.digibooky.controllers;

import org.metrunui.digibooky.domain.Book;
import org.metrunui.digibooky.repository.BookRepository;
import org.metrunui.digibooky.service.BookService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class BookControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private BookService service;
    @Autowired
    private BookRepository repository;

    @Test
    void getAllBooks_returnsBooksWithIsbnTitleAndAuthorName() throws Exception {
        // Arrange: put data in the system
        repository.save(new Book("9780321146533", "Test Driven Development", "Lorem ipsum", "Beck", "Kent"));

        // Act + Assert: call endpoint like a client
        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].isbn", hasItem("9780321146533")))
                .andExpect(jsonPath("$[*].title", hasItem("Test Driven Development")))
                .andExpect(jsonPath("$[*].authorFirstName", hasItem("Kent")))
                .andExpect(jsonPath("$[*].authorLastName", hasItem("Beck")))
                .andExpect(jsonPath("$[0].summary").doesNotExist());
    }

    @Test
    void getBook_returnsBooksWithIsbnTitleAuthorNameAndSummary() throws Exception {
        // Arrange: put data in the system
        Book book = new Book("9780321146533", "Test Driven Development", "Lorem ipsum", "Beck", "Kent");
        repository.save(book);

        // Act + Assert: call endpoint like a client
        mockMvc.perform(get("/books/" + book.getBookId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("isbn").value("9780321146533"))
                .andExpect(jsonPath("title").value("Test Driven Development"))
                .andExpect(jsonPath("authorFirstName").value("Kent"))
                .andExpect(jsonPath("authorLastName").value("Beck"))
                .andExpect(jsonPath("summary").value("Lorem ipsum"));
    }

    @Test
    void getBook_givenUnknownButCorrectlyFormattedId_returnsNotFoundResponse() throws Exception {
        // Act + Assert: call endpoint like a client
        mockMvc.perform(get("/books/5d19680d-47b6-4905-901d-358cf78fb371"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getBook_givenIncorrectlyFormattedId_returnsBadRequestResponse() throws Exception {
        // Act + Assert: call endpoint like a client
        mockMvc.perform(get("/books/badformat"))
                .andExpect(status().isBadRequest());
    }

}