package com.example.shopservice.resource;

import com.example.shopservice.entity.Order;
import com.example.shopservice.entity.OrderItem;
import com.example.shopservice.entity.Product;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/orders/{orderId}/items")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderItemResource {

    @GET
    public List<OrderItem> getAll(@PathParam("orderId") Long orderId) {
        return OrderItem.list("order.id", orderId);
    }

    @GET
    @Path("/{itemId}")
    public OrderItem get(@PathParam("orderId") Long orderId, @PathParam("itemId") Long itemId) {
        return OrderItem.find("order.id = ?1 and id = ?2", orderId, itemId).firstResult();
    }

    @POST
    @Transactional
    public Response create(@PathParam("orderId") Long orderId, OrderItem item) {
        Order order = Order.findById(orderId);
        if (order == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        if (item.product != null) {
            item.product = Product.findById(item.product.id);
        }
        item.order = order;
        item.persist();
        return Response.status(Response.Status.CREATED).entity(item).build();
    }

    @PUT
    @Path("/{itemId}")
    @Transactional
    public Response update(@PathParam("orderId") Long orderId, @PathParam("itemId") Long itemId, OrderItem updated) {
        OrderItem item = OrderItem.find("order.id = ?1 and id = ?2", orderId, itemId).firstResult();
        if (item == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        if (updated.product != null) {
            item.product = Product.findById(updated.product.id);
        }
        item.quantity = updated.quantity;
        return Response.ok(item).build();
    }

    @DELETE
    @Path("/{itemId}")
    @Transactional
    public Response delete(@PathParam("orderId") Long orderId, @PathParam("itemId") Long itemId) {
        OrderItem item = OrderItem.find("order.id = ?1 and id = ?2", orderId, itemId).firstResult();
        if (item == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        item.delete();
        return Response.noContent().build();
    }
}
