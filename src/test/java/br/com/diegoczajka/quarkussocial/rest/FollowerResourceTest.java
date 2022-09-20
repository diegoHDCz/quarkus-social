package br.com.diegoczajka.quarkussocial.rest;

import br.com.diegoczajka.quarkussocial.domain.model.Follower;
import br.com.diegoczajka.quarkussocial.domain.model.User;
import br.com.diegoczajka.quarkussocial.domain.repository.FollowerRepository;
import br.com.diegoczajka.quarkussocial.domain.repository.UserRepository;
import br.com.diegoczajka.quarkussocial.rest.dto.FollowerRequest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestHTTPEndpoint(FollowerResource.class)
class FollowerResourceTest {

    @Inject
    UserRepository userRepo;
    @Inject
    FollowerRepository followerRepo;
    private Long userId;
    private long followerId;

    @BeforeEach
    @Transactional
    void setUp() {
        var user = new User();
        user.setName("John Doe");
        user.setAge(30);
        userRepo.persist(user);
        userId = user.getId();

        var follower = new User();
        follower.setName("John Doe");
        follower.setAge(30);
        userRepo.persist(follower);
        followerId = follower.getId();

        //cria um follower
        var followerEntity = new Follower();
        followerEntity.setFollower(follower);
        followerEntity.setUser(user);
        followerRepo.persist(followerEntity);

    }

    @Test
    @DisplayName("Should not be able to follow yourself")
    public void FollowYorselfTest() {
        var body = new FollowerRequest();
        body.setFollowerId(userId);

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .pathParam("userId", userId)
                .when()
                .put()
                .then()
                .statusCode(Response.Status.CONFLICT.getStatusCode())
                .body(Matchers.is("Impossible to follow yourself"));


    }

    @Test
    @DisplayName("Should not be able to follow non existent user")
    public void FollowNonUserTest() {
        var body = new FollowerRequest();
        body.setFollowerId(userId);
        var inexistentUserId = 999;

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .pathParam("userId", inexistentUserId)
                .when()
                .put()
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());


    }

    @Test
    @DisplayName("Should be able to follow user")
    public void FollowUserTest() {
        var body = new FollowerRequest();
        body.setFollowerId(followerId);


        given()
                .contentType(ContentType.JSON)
                .body(body)
                .pathParam("userId", userId)
                .when()
                .put()
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());


    }

    @Test
    @DisplayName("Should not be able to list follower to a non existent user")
    public void UserNotFoundListFollowerTest() {

        var inexistentUserId = 999;

        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", inexistentUserId)
                .when()
                .get()
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());


    }

    @Test
    @DisplayName("Should not be able to list follower user's followers")
    public void UserListFollowerTest() {


        io.restassured.response.Response response = given()
                .contentType(ContentType.JSON)
                .pathParam("userId", userId)
                .when()
                .get()
                .then()
                .extract().response();

        var followersCount = response.jsonPath().get("followersCount");
        var followersContent = response.jsonPath().getList("content");
        assertEquals(Response.Status.OK.getStatusCode(), response.statusCode());
        assertEquals(1, followersCount);
        assertEquals(1, followersContent.size());


    }

    @Test
    @DisplayName("Should not be able to delete non existent user")
    public void InexsitentUserDeleteTest() {

        var inexistentUserId = 999;

        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", inexistentUserId)
                .queryParam("followerId", followerId)
                .when()
                .delete()
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());


    }

    @Test
    @DisplayName("Should  be able to delete  existent user")
    public void UnfollowingTest() {


        given()
                .pathParam("userId", userId)
                .queryParam("followerId", followerId)
                .when()
                .delete()
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());


    }

}