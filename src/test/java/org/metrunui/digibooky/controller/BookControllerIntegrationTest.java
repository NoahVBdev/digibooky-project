package org.metrunui.digibooky.controller;

import org.junit.jupiter.api.BeforeEach;
import org.metrunui.digibooky.domain.Book;
import org.metrunui.digibooky.repository.BookRepository;
import org.metrunui.digibooky.service.BookService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
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

    @BeforeEach
    void setUp() {
        repository.clear();
    }

    @Test
    void getAllBooks_returnsBooksWithIsbnTitleAndAuthorName() throws Exception {
        // Arrange: put data in the system
        Book book = new Book("9780321146533", "Test Driven Development", "Lorem ipsum", "Beck", "Kent");
        repository.save(book);

        // Act + Assert: call endpoint like a client
        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].bookId", hasItem(book.getBookId().toString())))
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
                .andExpect(jsonPath("bookId").value(book.getBookId().toString()))
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

    @Test
    void getBooks_givenValidIsbn_returnsBooksWithThatIsbn() throws Exception {
        // Arrange: put data in the system
        Book book1 = new Book("9780321146735", "Vibe Driven Development", "Lorem ipsum", "Kent", "Clark");
        Book book2 = new Book("9780320176536", "Test Driven Development", "Lorem ipsum", "Keith", "Caylen");
        Book book3 = new Book("9780301146537", "Magic Driven Development", "Lorem ipsum", "Beck", "Tom");
        Book other = new Book("9781234567897", "Impossible Driven Development", "Lorem ipsum", "Smith", "Jane");
        repository.save(book1);
        repository.save(book2);
        repository.save(book3);

        // Act + Assert: call endpoint like a client
        mockMvc.perform(get("/books").param("isbn", "9780320176536"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[*].isbn", contains("9780320176536")));
    }

    @Test
    void getBooks_givenIsbnWithTrailingWildcard_returnsAllBooksStartingWithThatIsbn() throws Exception {
        // Arrange: put data in the system
        Book book1 = new Book("9780321146735", "Vibe Driven Development", "Lorem ipsum", "Kent", "Clark");
        Book book2 = new Book("9780320176536", "Test Driven Development", "Lorem ipsum", "Keith", "Caylen");
        Book book3 = new Book("9780301146537", "Magic Driven Development", "Lorem ipsum", "Beck", "Tom");
        Book other = new Book("9781234567897", "Impossible Driven Development", "Lorem ipsum", "Smith", "Jane");
        repository.save(book1);
        repository.save(book2);
        repository.save(book3);
        repository.save(other);

        // Act + Assert: call endpoint like a client
        mockMvc.perform(get("/books").param("isbn", "97803*"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[*].isbn", containsInAnyOrder(
                        "9780321146735",
                        "9780320176536",
                        "9780301146537")));
    }

    @Test
    void getBooks_givenIsbnWithMiddleWildcard_returnsAllBooksContainingThatIsbn() throws Exception {
        // Arrange: put data in the system
        Book book1 = new Book("9780321146735", "Vibe Driven Development", "Lorem ipsum", "Kent", "Clark");
        Book book2 = new Book("9780320176536", "Test Driven Development", "Lorem ipsum", "Keith", "Caylen");
        Book book3 = new Book("9780301146537", "Magic Driven Development", "Lorem ipsum", "Beck", "Tom");
        Book other = new Book("9781234567897", "Impossible Driven Development", "Lorem ipsum", "Smith", "Jane");
        repository.save(book1);
        repository.save(book2);
        repository.save(book3);
        repository.save(other);

        // Act + Assert: call endpoint like a client
        mockMvc.perform(get("/books").param("isbn", "978*7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].isbn", containsInAnyOrder(
                        "9780301146537",
                        "9781234567897")));
    }

    @Test
    void getBooks_givenPartialIsbnWithoutWildcard_returnsEmptyList() throws Exception {
        // Arrange: put data in the system
        repository.save(new Book("9780321146535", "Vibe Driven Development", "Lorem ipsum", "Kent", "Clark"));

        // Act + Assert: call endpoint like a client
        mockMvc.perform(get("/books").param("isbn", "978032114653"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

}