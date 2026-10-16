package org.metrunui.digibooky.controllers;

import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.metrunui.digibooky.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.UUID;

import static io.restassured.RestAssured.given;

import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MemberControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @BeforeAll
    static void readErrorResponsesAsJson() {
        RestAssured.registerParser("application/problem+json", Parser.JSON);
    }

    @Test
    void registerMember_withValidData_returns201WithoutInss(){
        given().port(port).contentType(ContentType.JSON).body(validMemberJson())
        .when().post("/members")
        .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("lastName", equalTo("Becentral"))
                .body("city", equalTo("Bruxelles"))
                .body("inss", nullValue());
    }

    @Test
    void registerMember_withoutInss_returns400(){
        postAndExpect400(withField(validMemberJson(), "inss", ""), "INSS is required");
    }

    @Test
    void registerMember_withBlankLastName_returns400() {
        postAndExpect400(withField(validMemberJson(), "lastName", ""), "Last name is required");
    }

    @Test
    void registerMember_withBlankCity_returns400() {
        postAndExpect400(withField(validMemberJson(), "city", ""), "City is required");
    }

    @Test
    void registerMember_withInvalidEmail_returns400() {
        postAndExpect400(withField(validMemberJson(), "email", "not-an-email"), "Email must be of the form x@x.x");
    }

    @Test
    void registerMember_withAlreadyUsedEmail_returns400() {
        String first = validMemberJson();
        given().port(port).contentType(ContentType.JSON).body(first).post("/members").then().statusCode(201);

        String sameEmailOtherInss = withField(first, "inss", UUID.randomUUID().toString());
        postAndExpect400(sameEmailOtherInss, "email already exists");
    }

    @Test
    void registerMember_withAlreadyUsedInss_returns400() {
        String first = validMemberJson();
        given().port(port).contentType(ContentType.JSON).body(first).post("/members").then().statusCode(201);

        String sameInssOtherEmail = withField(first, "email", "other." + UUID.randomUUID().toString().substring(0, 8) + "@mail.be");
        postAndExpect400(sameInssOtherEmail, "INSS already exists");
    }

    @Test
    void getAllMembers_asAdmin_returns200WithMembersWithoutInss(){
        given().port(port).header("X-User-Id", UserRepository.DEFAULT_ADMIN_ID)
                .when()
                .get("/members")
                .then()
                .statusCode(200)
                .body("id", hasItem(UserRepository.DEFAULT_MEMBER_ID.toString()))
                .body("email", hasItem("member@digibooky.be"))
                .body("inss", everyItem(nullValue()));

    }

    @Test
    void getAllMembers_asAdmin_doesNotReturnAdminsOrLibrarians(){
        given().port(port).header("X-User-Id", UserRepository.DEFAULT_ADMIN_ID)
                .when()
                .get("/members")
                .then()
                .statusCode(200)
                .body("id", not(hasItem(UserRepository.DEFAULT_LIBRARIAN_ID.toString())))
                .body("id", not(hasItem(UserRepository.DEFAULT_ADMIN_ID.toString())));

    }

    @Test
    void getAllMembers_whenNewMembersCreatedToCheckIfTheListContainsThem(){
        String json = validMemberJson();
        String newId = given().port(port).contentType(ContentType.JSON)
                .body(json)
                .post("/members")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        given().port(port).header("X-User-id", UserRepository.DEFAULT_ADMIN_ID)
                .when()
                .get("/members")
                .then()
                .statusCode(200)
                .body("id", hasItem(newId));
    }

    @Test
    void getAllMembers_asLibrarian_returns403(){
        given().port(port).header("X-User-Id",UserRepository.DEFAULT_LIBRARIAN_ID)
                .when()
                .get("/members")
                .then()
                .statusCode(403)
                .body("detail", containsString("Not allowed"));

    }

    @Test
    void getAllMembers_asMember_returns403() {
        given().port(port).header("X-User-Id", UserRepository.DEFAULT_MEMBER_ID)
                .when().get("/members")
                .then()
                .statusCode(403)
                .body("detail", containsString("Not allowed"));
    }

    @Test
    void getAllMembers_withoutUserId_returns401(){
        given().port(port)
                .when().get("/members")
                .then()
                .statusCode(401)
                .body("detail", containsString("Must provide a user id"));

    }

    @Test
    void getAllMembers_withUnknowUserId_returns401(){
        given().port(port).header("X-User-Id", UUID.randomUUID())
                .when().get("/members")
                .then()
                .statusCode(401)
                .body("detail", containsString("Unknown user id"));

    }

    private void postAndExpect400(String json, String expectedMessage) {
        given().port(port).contentType(ContentType.JSON).body(json)
                .when().post("/members")
                .then()
                .statusCode(400)
                .body("detail", containsString(expectedMessage));
    }

    private static String validMemberJson() {
        String unique = UUID.randomUUID().toString().substring(0, 8);
        return """
                {
                  "inss": "%s",
                  "firstName": "Vincent",
                  "lastName": "Becentral",
                  "email": "vincent.%s@mail.be",
                  "streetName": "Rue de la Loi",
                  "streetNumber": "16",
                  "postalCode": "1000",
                  "city": "Bruxelles"
                }
                """.formatted(unique, unique);
    }

    private static String withField(String json, String field, String value) {
        return json.replaceAll("\"" + field + "\": \"[^\"]*\"", "\"" + field + "\": \"" + value + "\"");
    }

}