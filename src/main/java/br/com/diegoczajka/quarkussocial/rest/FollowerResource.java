package br.com.diegoczajka.quarkussocial.rest;

import br.com.diegoczajka.quarkussocial.domain.model.Follower;
import br.com.diegoczajka.quarkussocial.domain.model.User;
import br.com.diegoczajka.quarkussocial.domain.repository.FollowerRepository;
import br.com.diegoczajka.quarkussocial.domain.repository.UserRepository;
import br.com.diegoczajka.quarkussocial.rest.dto.FollowerPerUserResponse;
import br.com.diegoczajka.quarkussocial.rest.dto.FollowerRequest;
import br.com.diegoczajka.quarkussocial.rest.dto.FollowerResponse;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Path("/users/{userId}/followers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FollowerResource {
    private FollowerRepository followerRepository;
    private UserRepository userRepository;

    @Inject
    public FollowerResource(FollowerRepository followerRepository, UserRepository userRepository) {
        this.followerRepository = followerRepository;
        this.userRepository = userRepository;
    }

    @PUT
    @Transactional
    public Response followUser(@PathParam("userId") Long userId, FollowerRequest request) {

        if (userId.equals(request.getFollowerId())) {
            return Response.status(Response.Status.CONFLICT).entity("Impossible to follow yourself").build();
        }

        User userExists = userRepository.findById(userId);

        if (userExists == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        User followerExists = userRepository.findById(request.getFollowerId());

        boolean follows = followerRepository.follows(followerExists, userExists);

        if (!follows) {

            var entity = new Follower();
            entity.setUser(userExists);
            entity.setFollower(followerExists);

            followerRepository.persist(entity);
        }
        return Response.status(Response.Status.NO_CONTENT).build();

    }

    @GET
    public Response listFollower(@PathParam("userId") Long userId) {

        var user = userRepository.findById(userId);

        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        List<Follower> list = followerRepository.findByUser(userId);
        FollowerPerUserResponse responseObject = new FollowerPerUserResponse();
        responseObject.setFollowersCount(list.size());

        List<FollowerResponse> followerList = list.stream().map(FollowerResponse::new).collect(Collectors.toList());

        responseObject.setContent(followerList);
        return Response.ok(responseObject).build();

    }
}
