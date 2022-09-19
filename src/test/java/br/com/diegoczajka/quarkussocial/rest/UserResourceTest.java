package br.com.diegoczajka.quarkussocial.rest;

import br.com.diegoczajka.quarkussocial.rest.dto.CreateUserRequest;
import br.com.diegoczajka.quarkussocial.rest.dto.ResponseError;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;

import java.net.URL;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserResourceTest {

    @TestHTTPResource("/users")
    URL apiURL;

    @Test
    @DisplayName("Should be able to create an user")
    @Order(1)
    public void createUser() {
        var user = new CreateUserRequest();
        user.setName("John Doe");
        user.setAge(30);


        Response response = given().contentType(ContentType.JSON)
                .body(user)
                .when()
                .post(apiURL)
                .then().extract().response();

        assertEquals(201, response.getStatusCode());
        assertNotNull(response.jsonPath().getString("id"));
    }

    @Test
    @DisplayName("Should return error for invalid JSON")
    @Order(2)
    public void createUserValidationError() {
        var user = new CreateUserRequest();
        user.setAge(null);
        user.setName(null);

        Response response = given().contentType(ContentType.JSON)
                .body(user)
                .when()
                .post(apiURL)
                .then()
                .extract().response();

        assertEquals(ResponseError.UNPROCESSABLE_ENTITY_STATUS, response.getStatusCode());
        assertEquals("Validation Error", response.jsonPath().getString("message"));

        List<Map<String, String>> errors = response.jsonPath().getList("errors");

        assertNotNull(errors.get(0).get("message"));
        assertNotNull(errors.get(1).get("message"));

    }

    @Test
    @DisplayName("Should be able to list all users")
    @Order(3)
    public void listAllUsersTest() {
        given()
                .contentType(ContentType.JSON)
                .get(apiURL)
                .then()
                .statusCode(200)
                .body("size()", Matchers.is(1));

    }
}