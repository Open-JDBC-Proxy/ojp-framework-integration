package com.example.shopservice.resource;

import com.example.shopservice.dto.PageResult;
import com.example.shopservice.entity.OrderItem;
import com.example.shopservice.repository.OrderItemRepository;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/order-items")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderItemResource {

    @Inject
    private OrderItemRepository orderItemRepository;

    @POST
    public Response create(OrderItem orderItem) {
        OrderItem saved = orderItemRepository.save(orderItem);
        return Response.status(Response.Status.CREATED).entity(saved).build();
    }

    @GET
    public PageResult<OrderItem> list(@QueryParam("page") @DefaultValue("0") int page,
                                      @QueryParam("size") @DefaultValue("20") int size) {
        List<OrderItem> orderItems = orderItemRepository.findAll(page, size);
        long totalElements = orderItemRepository.count();
        return new PageResult<>(orderItems, page, size, totalElements);
    }

    @GET
    @Path("/{id}")
    public Response get(@PathParam("id") Long id) {
        return orderItemRepository.findById(id)
                .map(orderItem -> Response.ok(orderItem).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, OrderItem orderItem) {
        return orderItemRepository.findById(id)
                .map(existing -> {
                    existing.setQuantity(orderItem.getQuantity());
                    if (orderItem.getProduct() != null) {
                        existing.setProduct(orderItem.getProduct());
                    }
                    OrderItem updated = orderItemRepository.save(existing);
                    return Response.ok(updated).build();
                })
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        return orderItemRepository.findById(id)
                .map(existing -> {
                    orderItemRepository.delete(existing);
                    return Response.noContent().build();
                })
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }
}