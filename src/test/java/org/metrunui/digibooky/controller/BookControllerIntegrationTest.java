package org.metrunui.digibooky.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.metrunui.digibooky.controller.dto.BookDetailResponseDTO;
import org.metrunui.digibooky.controller.dto.CreateBookDTO;
import org.metrunui.digibooky.controller.mapper.BookMapper;
import org.metrunui.digibooky.domain.Book;
import org.metrunui.digibooky.repository.BookRepository;
import org.metrunui.digibooky.service.BookService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.stream.Stream;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    @Autowired
    private BookMapper bookMapper;

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

    @Nested
    @DisplayName("When creating a book")
    class BookCreation {

        @Nested
        @DisplayName("Given a librarian")
        class GivenLibrarian {

            @Test
            void givenLibrarianAndLegalValues_whenCreatingBook_thenBookCreated() throws Exception {
                // Given
                final String bookISBN = "9780321146735";
                final String bookTitle = "Integration testing for dummies";
                final String summary = "In this fun book we're checking out integration testing!";
                final String authorLastName = "Kent";
                final String authorFirstName = "Clark";
                CreateBookDTO request = new CreateBookDTO(bookISBN, bookTitle, summary, authorLastName, authorFirstName);

                // When + Then
                String bookAsString = mockMvc.perform(post("/books")
                                .header("X-User-id", "00000000-0000-0000-0000-000000000002")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(request)))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("bookId").exists())
                        .andExpect(jsonPath("isbn").value(bookISBN))
                        .andExpect(jsonPath("title").value(bookTitle))
                        .andExpect(jsonPath("summary").value(summary))
                        .andExpect(jsonPath("authorLastName").value(authorLastName))
                        .andExpect(jsonPath("authorFirstName").value(authorFirstName))
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

                BookDetailResponseDTO bookDetailResponseDTO = new ObjectMapper().readValue(bookAsString, BookDetailResponseDTO.class);
                Assertions.assertThat(repository.getBook(bookDetailResponseDTO.bookId())).isNotEmpty();

            }

            static Stream<Arguments> invalidRequests() {
                return Stream.of(
                        Arguments.of("isbn is required",
                                "isbn",
                                """
                                        {"isbn": "", "title": "Effective Java", "authorLastName": "Bloch", "authorFirstName": "Thomas"}"""),
                        Arguments.of("isbn is required",
                                "isbn",
                                """
                                        {"isbn": "    ", "title": "Effective Java", "authorLastName": "Bloch", "authorFirstName": "Thomas"}"""),
                        Arguments.of("Could not map to book: isbn should begin with valid prefix.",
                                "isbn",
                                """
                                        {"isbn": "1234567891014", "title": "Effective Java", "authorLastName": "Bloch", "authorFirstName": "Thomas"}"""),
                        Arguments.of("Could not map to book: isbn should be 13 digits.",
                                "isbn",
                                """
                                        {"isbn": "987456789101", "title": "Effective Java", "authorLastName": "Bloch", "authorFirstName": "Thomas"}"""),
                        Arguments.of("Could not map to book: isbn should be 13 digits.",
                                "isbn",
                                """
                                        {"isbn": "98745678910111", "title": "Effective Java", "authorLastName": "Bloch", "authorFirstName": "Thomas"}"""),
                        Arguments.of("Could not map to book: all isbn characters should be digits.",
                                "isbn",
                                """
                                        {"isbn": "978hello45678", "title": "Effective Java", "authorLastName": "Bloch", "authorFirstName": "Thomas"}"""),
                        Arguments.of("title is required",
                                "title",
                                """
                                        {"isbn": "9784563458615", "title": "", "authorLastName": "Bloch", "authorFirstName": "Thomas"}"""),
                        Arguments.of("title is required",
                                "title",
                                """
                                        {"isbn": "9784563458615", "title": "    ", "authorLastName": "Bloch", "authorFirstName": "Thomas"}"""),
                        Arguments.of("title is required",
                                "title",
                                """
                                        {"isbn": "9784563458615", "title": "    ", "authorLastName": "Bloch", "authorFirstName": "Thomas"}"""),
                        Arguments.of("author last name is required",
                                "authorLastName",
                                """
                                        {"isbn": "9784563458615", "title": "Effective Java", "authorLastName": "", "authorFirstName": "Thomas"}"""),
                        Arguments.of("author last name is required",
                                "authorLastName",
                                """
                                        {"isbn": "9784563458615", "title": "Effective Java", "authorLastName": "   ", "authorFirstName": "Thomas"}"""),
                        Arguments.of("author first name is required",
                                "authorFirstName",
                                """
                                        {"isbn": "9784563458615", "title": "Effective Java", "authorLastName": "Bloch", "authorFirstName": ""}"""),
                        Arguments.of("author first name is required",
                                "authorFirstName",
                                """
                                        {"isbn": "9784563458615", "title": "Effective Java", "authorLastName": "Bloch", "authorFirstName": "   "}""")
                );
            }

            @ParameterizedTest(name = "400 bad request: {0} given illegal value in field {1}")
            @MethodSource("org.metrunui.digibooky.controller.BookControllerIntegrationTest$BookCreation$GivenLibrarian#invalidRequests")
            void givenIllegalValuesForRequiredFieldsAndLibrarian_whenCreatingBook_thenReturnBadRequest(String errorDetail, String wrongParameter, String createBookDTO) throws Exception {
                // Given + When + Then
                mockMvc.perform(post("/books")
                                .header("X-User-id", "00000000-0000-0000-0000-000000000002")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(createBookDTO))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("detail").value(errorDetail));
            }
        }


        @Test
        void givenValidUserButNonLibrarianAndLegalValues_whenCreatingBook_thenReturnForbiddenResponse() throws Exception {
            // Given
            final String bookISBN = "9780321146735";
            final String bookTitle = "Integration testing for dummies";
            final String summary = "In this fun book we're checking out integration testing!";
            final String authorLastName = "Kent";
            final String authorFirstName = "Clark";

            CreateBookDTO request = new CreateBookDTO(bookISBN, bookTitle, summary, authorLastName, authorFirstName);

            // When + Then
            mockMvc.perform(post("/books")
                            .header("X-User-id", "00000000-0000-0000-0000-000000000003")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(request)))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("detail").value("You're not allowed to do this"));
        }

        @Test
        void givenUnknownUserAndLegalValues_whenCreatingBook_thenReturnUnauthorizedResponse() throws Exception {
            // Given
            final String bookISBN = "9780321146735";
            final String bookTitle = "Integration testing for dummies";
            final String summary = "In this fun book we're checking out integration testing!";
            final String authorLastName = "Kent";
            final String authorFirstName = "Clark";

            CreateBookDTO request = new CreateBookDTO(bookISBN, bookTitle, summary, authorLastName, authorFirstName);

            // When + Then
            mockMvc.perform(post("/books")
                            .header("X-User-id", "00000000-0000-0000-0000-000000000000")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(request)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("detail").value("Unknown user id"));
        }

    }

}