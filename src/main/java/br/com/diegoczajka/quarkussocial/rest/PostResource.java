package br.com.diegoczajka.quarkussocial.rest;

import br.com.diegoczajka.quarkussocial.domain.model.Post;
import br.com.diegoczajka.quarkussocial.domain.model.User;
import br.com.diegoczajka.quarkussocial.domain.repository.FollowerRepository;
import br.com.diegoczajka.quarkussocial.domain.repository.PostRepository;
import br.com.diegoczajka.quarkussocial.domain.repository.UserRepository;
import br.com.diegoczajka.quarkussocial.rest.dto.CreatePostRequest;
import br.com.diegoczajka.quarkussocial.rest.dto.PostResponse;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Path("/users/{userId}/posts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PostResource {

    private UserRepository userRepository;

    private PostRepository postRepository;
    private FollowerRepository followerRepository;

    @Inject
    public PostResource(UserRepository userRepository, PostRepository postRepository, FollowerRepository followerRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.followerRepository = followerRepository;
    }

    @GET
    public Response listPosts(
            @PathParam("userId") Long userId,
            @HeaderParam("followerId") Long followerId
    ) {
        User userFound = userRepository.findById(userId);
        if (userFound == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        if (followerId == null) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("Missing Header followerId")
                    .build();
        }

        User follower = userRepository.findById(followerId);

        if (follower == null) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("Follower doesn't exists")
                    .build();
        }

        boolean follows = followerRepository.follows(follower, userFound);

        if (!follows) {
            return Response.status(Response.Status.FORBIDDEN).entity("You can't see postos from whom you don't follow").build();
        }


        //postRepository.find("select post from Posts where user = :user");
        PanacheQuery<Post> query =
                postRepository.
                        find("user",
                                Sort.by("dateTime", Sort.Direction.Descending),
                                userFound);

        var list = query.list();

        //var dataResponse
        List<PostResponse> dataResponse = list.stream()
                //.map(post -> PostResponse.fromEntity(post))
                .map(PostResponse::fromEntity)
                .collect(Collectors.toList());

        return Response.ok(dataResponse).build();
    }

    @POST
    @Transactional
    public Response createPost(@PathParam("userId") Long userId, CreatePostRequest request) {
        User userFound = userRepository.findById(userId);
        if (userFound == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        Post post = new Post();
        post.setText(request.getText());
        post.setUser(userFound);

        postRepository.persist(post);

        PostResponse dataResponse = PostResponse.fromEntity(post);

        return Response.status(Response.Status.CREATED).entity(dataResponse).build();
    }
}
