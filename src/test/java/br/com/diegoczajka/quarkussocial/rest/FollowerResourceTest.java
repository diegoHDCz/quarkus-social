package br.com.diegoczajka.quarkussocial.rest;

import br.com.diegoczajka.quarkussocial.domain.model.User;
import br.com.diegoczajka.quarkussocial.domain.repository.FollowerRepository;
import br.com.diegoczajka.quarkussocial.domain.repository.UserRepository;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestHTTPEndpoint(FollowerResource.class)
class FollowerResourceTest {

    @Inject
    UserRepository userRepo;
    @Inject
    FollowerRepository followerRepo;
    private Long userId;

    @BeforeEach
    void setUp() {
        var user = new User();
        user.setName("John Doe");
        user.setAge(30);
    }

}