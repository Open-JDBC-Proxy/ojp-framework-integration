package com.example.shopservice.resource;

import com.example.shopservice.entity.Order;
import com.example.shopservice.entity.User;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.List;

@Path("/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderResource {

    @GET
    public List<Order> getAll() {
        return Order.listAll();
    }

    @GET
    @Path("/{id}")
    public Order get(@PathParam("id") Long id) {
        return Order.findById(id);
    }

    @POST
    @Transactional
    public Response create(Order order) {
        if (order.user != null) {
            order.user = User.findById(order.user.id);
        }
        order.createdAt = LocalDateTime.now();
        order.persist();
        return Response.status(Response.Status.CREATED).entity(order).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response update(@PathParam("id") Long id, Order updated) {
        Order order = Order.findById(id);
        if (order == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        if (updated.user != null) {
            order.user = User.findById(updated.user.id);
        }
        return Response.ok(order).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response delete(@PathParam("id") Long id) {
        boolean deleted = Order.deleteById(id);
        return deleted ? Response.noContent().build() : Response.status(Response.Status.NOT_FOUND).build();
    }
}
