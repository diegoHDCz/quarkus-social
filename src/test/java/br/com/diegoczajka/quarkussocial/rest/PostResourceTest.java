package br.com.diegoczajka.quarkussocial.rest;

import br.com.diegoczajka.quarkussocial.domain.model.Follower;
import br.com.diegoczajka.quarkussocial.domain.model.Post;
import br.com.diegoczajka.quarkussocial.domain.model.User;
import br.com.diegoczajka.quarkussocial.domain.repository.FollowerRepository;
import br.com.diegoczajka.quarkussocial.domain.repository.PostRepository;
import br.com.diegoczajka.quarkussocial.domain.repository.UserRepository;
import br.com.diegoczajka.quarkussocial.rest.dto.CreatePostRequest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.transaction.Transactional;

import static io.restassured.RestAssured.given;

@QuarkusTest
@TestHTTPEndpoint(PostResource.class)
class PostResourceTest {

    @Inject
    UserRepository userRepository;
    @Inject
    FollowerRepository followerRepo;
    @Inject
    PostRepository postRepo;
    Long userID;

    Long userNotFollowerId;
    Long userFollowerId;


    @BeforeEach
    @Transactional
    public void setUp() {
        var user = new User();
        user.setAge(30);
        user.setName("John Doe");
        userRepository.persist(user);
        userID = user.getId();

        var userNotFollower = new User();
        userNotFollower.setAge(30);
        userNotFollower.setName("John Doe");
        userRepository.persist(userNotFollower);
        userNotFollowerId = userNotFollower.getId();

        var userFollower = new User();
        userFollower.setAge(30);
        userFollower.setName("John Doe");
        userRepository.persist(userFollower);
        userFollowerId = userFollower.getId();

        Follower follower = new Follower();
        follower.setUser(user);
        follower.setFollower(userFollower);

        followerRepo.persist(follower);

        Post post = new Post();
        post.setText("Hello World!");
        post.setUser(user);
        postRepo.persist(post);
    }

    @Test
    @DisplayName("Should be albe to create post for an user")
    public void createPost() {
        var postResquest = new CreatePostRequest();


        postResquest.setText("some text about vacation");
        given()
                .contentType(ContentType.JSON)
                .body(postResquest)
                .pathParam("userId", userID)
                .when()
                .post()
                .then()
                .statusCode(201);
    }

    @Test
    @DisplayName("Should not be able to create post for inexstente user")
    public void errorPostRest() {
        var postResquest = new CreatePostRequest();

        var inexistentUserId = 999;

        postResquest.setText("some text about vacation");
        given()
                .contentType(ContentType.JSON)
                .body(postResquest)
                .pathParam("userId", inexistentUserId)
                .when()
                .post()
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("Should not be able to list posts for nonexistent user")
    public void listPostsUserNotFound() {
        var inexistentUserId = 999;


        given()
                .pathParam("userId", inexistentUserId)
                .when()
                .get()
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("Should not be able to list posts when missing followerId")
    public void listPostsFollowerHeaderNotSend() {

        given()
                .pathParam("userId", userID)
                .when()
                .get()
                .then()
                .statusCode(400)
                .body(Matchers.is("Missing Header followerId"));
    }

    @Test
    @DisplayName("Should not be able to list posts with nonexistente follower")
    public void listPostsFollowerDoesNotExists() {

        var inexistenteFollowerId = 999;

        given()
                .pathParam("userId", userID)
                .header("followerId", inexistenteFollowerId)
                .when()
                .get()
                .then()
                .statusCode(400)
                .body(Matchers.is("Follower doesn't exists"));
    }

    @Test
    @DisplayName("Should not be able to list posts for person that is not a follower")
    public void listPostsNotFollower() {


        given()
                .pathParam("userId", userID)
                .header("followerId", userNotFollowerId)
                .when()
                .get()
                .then()
                .statusCode(403)
                .body(Matchers.is("You can't see postos from whom you don't follow"));
    }

    @Test
    @DisplayName("Should be able to list posts")
    public void listPostsTest() {
        given()
                .pathParam("userId", userID)
                .header("followerId", userFollowerId)
                .when()
                .get()
                .then()
                .statusCode(200)
                .body("size()", Matchers.is(1));

    }

}