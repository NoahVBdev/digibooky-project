package org.metrunui.digibooky.controller;

import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.metrunui.digibooky.domain.Lending;
import org.metrunui.digibooky.repository.LendingRepository;
import org.metrunui.digibooky.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.Clock;
import java.time.LocalDate;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LendingControllerReturnIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private LendingRepository lendingRepository;

    @Autowired
    private Clock clock;

    @BeforeAll
    static void readErrorResponsesAsJson() {
        RestAssured.registerParser("application/problem+json", Parser.JSON);
    }

    private Lending saveLending(UUID memberId, LocalDate dueDate) {
        Lending lending = new Lending(UUID.randomUUID(), memberId, UUID.randomUUID(),
                dueDate.minusWeeks(Lending.LENDING_PERIOD_WEEKS), dueDate);
        return lendingRepository.save(lending);
    }

    @Test
    void returnBook_whenReturnedOnTime_thenReturns200NotLate() {
        LocalDate today = LocalDate.now(clock);
        Lending lending = saveLending(UserRepository.DEFAULT_MEMBER_ID, today.plusDays(5));

        given().port(port).header("X-User-Id", UserRepository.DEFAULT_MEMBER_ID)
                .when().post("/lendings/" + lending.getId() + "/return")
                .then()
                .statusCode(200)
                .body("lendingId", equalTo(lending.getId().toString()))
                .body("bookId", equalTo(lending.getBookId().toString()))
                .body("dueDate", equalTo(today.plusDays(5).toString()))
                .body("returnDate", equalTo(today.toString()))
                .body("late", equalTo(false))
                .body("message", equalTo("Book returned on time."));
    }

    @Test
    void returnBook_whenReturnedLate_thenReturns200Late() {
        LocalDate today = LocalDate.now(clock);
        LocalDate dueDate = today.minusWeeks(1);
        Lending lending = saveLending(UserRepository.DEFAULT_MEMBER_ID, dueDate);

        given().port(port).header("X-User-Id", UserRepository.DEFAULT_MEMBER_ID)
                .when().post("/lendings/" + lending.getId() + "/return")
                .then()
                .statusCode(200)
                .body("late", equalTo(true))
                .body("message", equalTo("Book returned late. It was due on " + dueDate + "."));
    }

    @Test
    void returnBook_whenNoUserId_thenReturns401() {
        Lending lending = saveLending(UserRepository.DEFAULT_MEMBER_ID, LocalDate.now(clock).plusDays(5));

        given().port(port)
                .when().post("/lendings/" + lending.getId() + "/return")
                .then()
                .statusCode(401);
    }

    @Test
    void returnBook_whenLibrarian_thenReturns403() {
        Lending lending = saveLending(UserRepository.DEFAULT_MEMBER_ID, LocalDate.now(clock).plusDays(5));

        given().port(port).header("X-User-Id", UserRepository.DEFAULT_LIBRARIAN_ID)
                .when().post("/lendings/" + lending.getId() + "/return")
                .then()
                .statusCode(403);
    }

    @Test
    void returnBook_whenLendingOfAnotherMember_thenReturns403() {
        Lending lending = saveLending(UUID.randomUUID(), LocalDate.now(clock).plusDays(5));

        given().port(port).header("X-User-Id", UserRepository.DEFAULT_MEMBER_ID)
                .when().post("/lendings/" + lending.getId() + "/return")
                .then()
                .statusCode(403)
                .body("detail", equalTo("You can only return your own lendings"));
    }

    @Test
    void returnBook_whenUnknownLendingId_thenReturns404() {
        UUID unknownId = UUID.randomUUID();

        given().port(port).header("X-User-Id", UserRepository.DEFAULT_MEMBER_ID)
                .when().post("/lendings/" + unknownId + "/return")
                .then()
                .statusCode(404)
                .body("detail", equalTo("Lending with id " + unknownId + " not found"));
    }

    @Test
    void returnBook_whenAlreadyReturned_thenReturns400() {
        Lending lending = saveLending(UserRepository.DEFAULT_MEMBER_ID, LocalDate.now(clock).plusDays(5));
        lending.returnBook(LocalDate.now(clock));

        given().port(port).header("X-User-Id", UserRepository.DEFAULT_MEMBER_ID)
                .when().post("/lendings/" + lending.getId() + "/return")
                .then()
                .statusCode(400)
                .body("detail", equalTo("This lending has already been returned"));
    }

    @Test
    void returnBook_whenMalformedLendingId_thenReturns400() {
        given().port(port).header("X-User-Id", UserRepository.DEFAULT_MEMBER_ID)
                .when().post("/lendings/not-a-uuid/return")
                .then()
                .statusCode(400);
    }
}