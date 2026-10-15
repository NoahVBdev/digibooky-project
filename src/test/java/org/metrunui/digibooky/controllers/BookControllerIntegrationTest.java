package org.metrunui.digibooky.controllers;

import org.metrunui.digibooky.domain.Book;
import org.metrunui.digibooky.repository.BookRepository;
import org.metrunui.digibooky.service.BookService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import org.springframework.test.web.servlet.MockMvc;

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

}