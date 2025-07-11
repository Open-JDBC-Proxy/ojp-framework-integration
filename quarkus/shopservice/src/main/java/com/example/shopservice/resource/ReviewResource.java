package com.example.shopservice.resource;

import com.example.shopservice.entity.Product;
import com.example.shopservice.entity.Review;
import com.example.shopservice.entity.User;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/reviews")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReviewResource {

    @GET
    public List<Review> getAll() {
        return Review.listAll();
    }

    @GET
    @Path("/{id}")
    public Review get(@PathParam("id") Long id) {
        return Review.findById(id);
    }

    @POST
    @Transactional
    public Response create(Review review) {
        if (review.user != null) {
            review.user = User.findById(review.user.id);
        }
        if (review.product != null) {
            review.product = Product.findById(review.product.id);
        }
        review.persist();
        return Response.status(Response.Status.CREATED).entity(review).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response update(@PathParam("id") Long id, Review updated) {
        Review review = Review.findById(id);
        if (review == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        if (updated.user != null) {
            review.user = User.findById(updated.user.id);
        }
        if (updated.product != null) {
            review.product = Product.findById(updated.product.id);
        }
        review.rating = updated.rating;
        review.comment = updated.comment;
        return Response.ok(review).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response delete(@PathParam("id") Long id) {
        boolean deleted = Review.deleteById(id);
        return deleted ? Response.noContent().build() : Response.status(Response.Status.NOT_FOUND).build();
    }
}
