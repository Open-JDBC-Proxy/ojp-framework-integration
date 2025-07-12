package com.example.shopservice.resource;

import com.example.shopservice.dto.PageResult;
import com.example.shopservice.entity.Product;
import com.example.shopservice.repository.ProductRepository;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/products")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductResource {

    @Inject
    private ProductRepository productRepository;

    @POST
    public Response create(Product product) {
        Product saved = productRepository.save(product);
        return Response.status(Response.Status.CREATED).entity(saved).build();
    }

    @GET
    public PageResult<Product> list(@QueryParam("page") @DefaultValue("0") int page,
                                    @QueryParam("size") @DefaultValue("20") int size) {
        List<Product> products = productRepository.findAll(page, size);
        long totalElements = productRepository.count();
        return new PageResult<>(products, page, size, totalElements);
    }

    @GET
    @Path("/{id}")
    public Response get(@PathParam("id") Long id) {
        return productRepository.findById(id)
                .map(product -> Response.ok(product).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, Product product) {
        return productRepository.findById(id)
                .map(existing -> {
                    existing.setName(product.getName());
                    existing.setPrice(product.getPrice());
                    Product updated = productRepository.save(existing);
                    return Response.ok(updated).build();
                })
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        return productRepository.findById(id)
                .map(existing -> {
                    productRepository.delete(existing);
                    return Response.noContent().build();
                })
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }
}