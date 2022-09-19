package br.com.diegoczajka.quarkussocial.rest;

import br.com.diegoczajka.quarkussocial.rest.dto.CreateUserRequest;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
class UserResourceTest {

    @Test
    @DisplayName("Should be able to create an user")
    public void createUser() {
        var user = new CreateUserRequest();
        user.setName("John Doe");
        user.setAge(30);


        Response response = given().contentType(ContentType.JSON)
                .body(user)
                .when()
                .post("users")
                .then().extract().response();

        assertEquals(201, response.getStatusCode());
        assertNotNull(response.jsonPath().getString("id"));
    }
}