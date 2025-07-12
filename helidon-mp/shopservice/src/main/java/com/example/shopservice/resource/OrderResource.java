package com.example.shopservice.resource;

import com.example.shopservice.dto.PageResult;
import com.example.shopservice.entity.Order;
import com.example.shopservice.entity.OrderItem;
import com.example.shopservice.entity.Product;
import com.example.shopservice.entity.User;
import com.example.shopservice.repository.OrderRepository;
import com.example.shopservice.repository.ProductRepository;
import com.example.shopservice.repository.UserRepository;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;

@Path("/orders")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderResource {

    @Inject
    private OrderRepository orderRepository;
    
    @Inject
    private UserRepository userRepository;
    
    @Inject
    private ProductRepository productRepository;

    @POST
    public Response create(Order order) {
        if (order.getUser() == null || order.getUser().getId() == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        
        Optional<User> userOpt = userRepository.findById(order.getUser().getId());
        if (userOpt.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        order.setUser(userOpt.get());

        List<OrderItem> items = order.getOrderItems();
        if (items != null) {
            for (OrderItem item : items) {
                if (item.getProduct() == null || item.getProduct().getId() == null) {
                    return Response.status(Response.Status.BAD_REQUEST).build();
                }
                Optional<Product> prodOpt = productRepository.findById(item.getProduct().getId());
                if (prodOpt.isEmpty()) {
                    return Response.status(Response.Status.BAD_REQUEST).build();
                }
                item.setProduct(prodOpt.get());
                item.setOrder(order);
            }
        }
        
        Order saved = orderRepository.save(order);
        return Response.status(Response.Status.CREATED).entity(saved).build();
    }

    @GET
    public PageResult<Order> list(@QueryParam("page") @DefaultValue("0") int page,
                                  @QueryParam("size") @DefaultValue("20") int size) {
        List<Order> orders = orderRepository.findAll(page, size);
        long totalElements = orderRepository.count();
        return new PageResult<>(orders, page, size, totalElements);
    }

    @GET
    @Path("/{id}")
    public Response get(@PathParam("id") Long id) {
        return orderRepository.findById(id)
                .map(order -> Response.ok(order).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, Order order) {
        return orderRepository.findById(id)
                .map(existing -> {
                    if (order.getUser() != null && order.getUser().getId() != null) {
                        Optional<User> userOpt = userRepository.findById(order.getUser().getId());
                        userOpt.ifPresent(existing::setUser);
                    }
                    if (order.getOrderDate() != null) {
                        existing.setOrderDate(order.getOrderDate());
                    }
                    Order updated = orderRepository.save(existing);
                    return Response.ok(updated).build();
                })
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        return orderRepository.findById(id)
                .map(existing -> {
                    orderRepository.delete(existing);
                    return Response.noContent().build();
                })
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }
}