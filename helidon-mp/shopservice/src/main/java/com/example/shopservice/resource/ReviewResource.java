package com.example.shopservice.resource;

import com.example.shopservice.dto.PageResult;
import com.example.shopservice.entity.Review;
import com.example.shopservice.repository.ReviewRepository;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/reviews")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReviewResource {

    @Inject
    private ReviewRepository reviewRepository;

    @POST
    public Response create(Review review) {
        Review saved = reviewRepository.save(review);
        return Response.status(Response.Status.CREATED).entity(saved).build();
    }

    @GET
    public PageResult<Review> list(@QueryParam("page") @DefaultValue("0") int page,
                                   @QueryParam("size") @DefaultValue("20") int size) {
        List<Review> reviews = reviewRepository.findAll(page, size);
        long totalElements = reviewRepository.count();
        return new PageResult<>(reviews, page, size, totalElements);
    }

    @GET
    @Path("/{id}")
    public Response get(@PathParam("id") Long id) {
        return reviewRepository.findById(id)
                .map(review -> Response.ok(review).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, Review review) {
        return reviewRepository.findById(id)
                .map(existing -> {
                    existing.setRating(review.getRating());
                    existing.setComment(review.getComment());
                    if (review.getUser() != null) {
                        existing.setUser(review.getUser());
                    }
                    if (review.getProduct() != null) {
                        existing.setProduct(review.getProduct());
                    }
                    Review updated = reviewRepository.save(existing);
                    return Response.ok(updated).build();
                })
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        return reviewRepository.findById(id)
                .map(existing -> {
                    reviewRepository.delete(existing);
                    return Response.noContent().build();
                })
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }
}