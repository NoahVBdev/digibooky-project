package org.metrunui.digibooky.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.metrunui.digibooky.domain.Book;
import org.metrunui.digibooky.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.Clock;
import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.metrunui.digibooky.repository.UserRepository.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LendingControllerLendIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private Clock clock;

    @BeforeAll
    static void readErrorResponsesAsJson() {
        RestAssured.registerParser("application/problem+json", Parser.JSON);
    }

    @Test
    void lendBook_asMember_returns201WithDueDateInThreeWeeks() {
        Book book = saveNewBook();
        LocalDate today = LocalDate.now(clock);

        given().port(port).header("X-User-Id", DEFAULT_MEMBER_ID)
                .contentType(ContentType.JSON).body(lendJson(book.getIsbn()))
                .when().post("/lendings")
                .then()
                .statusCode(201)
                .body("lendingId", notNullValue())
                .body("memberId", equalTo(DEFAULT_MEMBER_ID.toString()))
                .body("bookId", equalTo(book.getBookId().toString()))
                .body("lendingDate", equalTo(today.toString()))
                .body("dueDate", equalTo(today.plusWeeks(3).toString()));
    }

    @Test
    void lendBook_whenBookAlreadyLent_returns400() {
        Book book = saveNewBook();
        given().port(port).header("X-User-Id", DEFAULT_MEMBER_ID)
                .contentType(ContentType.JSON).body(lendJson(book.getIsbn()))
                .post("/lendings").then().statusCode(201);

        given().port(port).header("X-User-Id", DEFAULT_MEMBER_ID)
                .contentType(ContentType.JSON).body(lendJson(book.getIsbn()))
                .when().post("/lendings")
                .then()
                .statusCode(400)
                .body("detail", containsString("already lent"));
    }

    @Test
    void lendBook_whenIsbnUnknown_returns404() {
        given().port(port).header("X-User-Id", DEFAULT_MEMBER_ID)
                .contentType(ContentType.JSON).body(lendJson(uniqueIsbn()))
                .when().post("/lendings")
                .then()
                .statusCode(404)
                .body("detail", containsString("not found"));
    }

    @Test
    void lendBook_withBlankIsbn_returns400() {
        given().port(port).header("X-User-Id", DEFAULT_MEMBER_ID)
                .contentType(ContentType.JSON).body(lendJson(""))
                .when().post("/lendings")
                .then()
                .statusCode(400)
                .body("detail", containsString("ISBN is required"));
    }

    @Test
    void lendBook_withoutUserId_returns401() {
        given().port(port)
                .contentType(ContentType.JSON).body(lendJson(saveNewBook().getIsbn()))
                .when().post("/lendings")
                .then()
                .statusCode(401);
    }

    @Test
    void lendBook_asLibrarian_returns403() {
        given().port(port).header("X-User-Id", DEFAULT_LIBRARIAN_ID)
                .contentType(ContentType.JSON).body(lendJson(saveNewBook().getIsbn()))
                .when().post("/lendings")
                .then()
                .statusCode(403);
    }

    @Test
    void lendBook_asAdmin_returns403() {
        given().port(port).header("X-User-Id", DEFAULT_ADMIN_ID)
                .contentType(ContentType.JSON).body(lendJson(saveNewBook().getIsbn()))
                .when().post("/lendings")
                .then()
                .statusCode(403);
    }


    private Book saveNewBook() {
        return bookRepository.save(new Book(uniqueIsbn(), "Test book", "A summary", "Doe", "John"));
    }

    private static String uniqueIsbn() {
        return "978" + String.format("%010d", ThreadLocalRandom.current().nextLong(10_000_000_000L));
    }

    private static String lendJson(String isbn) {
        return """
                { "isbn": "%s" }
                """.formatted(isbn);
    }
}