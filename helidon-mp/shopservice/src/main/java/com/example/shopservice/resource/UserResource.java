package com.example.shopservice.resource;

import com.example.shopservice.dto.PageResult;
import com.example.shopservice.entity.User;
import com.example.shopservice.repository.UserRepository;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/users")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    @Inject
    private UserRepository userRepository;

    @POST
    public Response create(User user) {
        User saved = userRepository.save(user);
        return Response.status(Response.Status.CREATED).entity(saved).build();
    }

    @GET
    public PageResult<User> list(@QueryParam("page") @DefaultValue("0") int page,
                                 @QueryParam("size") @DefaultValue("20") int size) {
        List<User> users = userRepository.findAll(page, size);
        long totalElements = userRepository.count();
        return new PageResult<>(users, page, size, totalElements);
    }

    @GET
    @Path("/{id}")
    public Response get(@PathParam("id") Long id) {
        return userRepository.findById(id)
                .map(user -> Response.ok(user).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, User user) {
        return userRepository.findById(id)
                .map(existing -> {
                    existing.setUsername(user.getUsername());
                    existing.setEmail(user.getEmail());
                    User updated = userRepository.save(existing);
                    return Response.ok(updated).build();
                })
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        return userRepository.findById(id)
                .map(existing -> {
                    userRepository.delete(existing);
                    return Response.noContent().build();
                })
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }
}