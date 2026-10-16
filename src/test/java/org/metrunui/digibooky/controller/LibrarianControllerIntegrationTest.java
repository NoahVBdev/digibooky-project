package org.metrunui.digibooky.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.metrunui.digibooky.repository.UserRepository;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LibrarianControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @BeforeAll
    static void readErrorResponsesAsJson() {
        RestAssured.registerParser("application/problem+json", Parser.JSON);
    }

    @Test
    void registerLibrarian_asAdmin_returns201WithLibrarianRole() {
        given().port(port).header("X-User-Id", UserRepository.DEFAULT_ADMIN_ID)
                .contentType(ContentType.JSON).body(librarianJson(uniqueEmail()))
                .when().post("/librarians")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("firstname", equalTo("Jane"))
                .body("role", equalTo("LIBRARIAN"));
    }

    @Test
    void registerLibrarian_ignoresRoleSentByClient() {
        String json = """
                {"firstname": "Evil", "lastname": "Hacker", "email": "%s", "role": "ADMIN"}
                """.formatted(uniqueEmail());

        given().port(port).header("X-User-Id", UserRepository.DEFAULT_ADMIN_ID)
                .contentType(ContentType.JSON).body(json)
                .when().post("/librarians")
                .then()
                .statusCode(201)
                .body("role", equalTo("LIBRARIAN"));
    }

    @Test
    void registerLibrarian_withoutUserId_returns401() {
        given().port(port)
                .contentType(ContentType.JSON).body(librarianJson(uniqueEmail()))
                .when().post("/librarians")
                .then()
                .statusCode(401)
                .body("detail", containsString("Must provide a user id"));
    }

    @Test
    void registerLibrarian_withUnknownUserId_returns401() {
        given().port(port).header("X-User-Id", UUID.randomUUID())
                .contentType(ContentType.JSON).body(librarianJson(uniqueEmail()))
                .when().post("/librarians")
                .then()
                .statusCode(401)
                .body("detail", containsString("Unknown user id"));
    }

    @Test
    void registerLibrarian_asMember_returns403() {
        given().port(port).header("X-User-Id", UserRepository.DEFAULT_MEMBER_ID)
                .contentType(ContentType.JSON).body(librarianJson(uniqueEmail()))
                .when().post("/librarians")
                .then()
                .statusCode(403)
                .body("detail", containsString("Not allowed"));
    }

    @Test
    void registerLibrarian_asLibrarian_returns403() {
        given().port(port).header("X-User-Id", UserRepository.DEFAULT_LIBRARIAN_ID)
                .contentType(ContentType.JSON).body(librarianJson(uniqueEmail()))
                .when().post("/librarians")
                .then()
                .statusCode(403);
    }

    @Test
    void registerLibrarian_withAlreadyUsedEmail_returns400() {
        given().port(port).header("X-User-Id", UserRepository.DEFAULT_ADMIN_ID)
                .contentType(ContentType.JSON).body(librarianJson("librarian@digibooky.be"))
                .when().post("/librarians")
                .then()
                .statusCode(400)
                .body("detail", containsString("email already exists"));
    }

    @Test
    void registerLibrarian_withBlankFirstname_returns400() {
        String json = """
                {"firstname": "", "lastname": "Doe", "email": "%s"}
                """.formatted(uniqueEmail());
        postAsAdminAndExpect400(json);
    }

    @Test
    void registerLibrarian_withBlankLastname_returns400() {
        String json = """
                {"firstname": "Jane", "lastname": "", "email": "%s"}
                """.formatted(uniqueEmail());
        postAsAdminAndExpect400(json);
    }

    @Test
    void registerLibrarian_withInvalidEmail_returns400() {
        postAsAdminAndExpect400(librarianJson("not-an-email"));
    }

    private void postAsAdminAndExpect400(String json) {
        given().port(port).header("X-User-Id", UserRepository.DEFAULT_ADMIN_ID)
                .contentType(ContentType.JSON).body(json)
                .when().post("/librarians")
                .then()
                .statusCode(400);
    }

    private static String uniqueEmail() {
        return "jane." + UUID.randomUUID().toString().substring(0, 8) + "@digibooky.be";
    }

    private static String librarianJson(String email) {
        return """
                {"firstname": "Jane", "lastname": "Doe", "email": "%s"}
                """.formatted(email);
    }
}