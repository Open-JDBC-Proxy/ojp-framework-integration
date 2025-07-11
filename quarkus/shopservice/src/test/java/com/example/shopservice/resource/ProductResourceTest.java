package com.example.shopservice.resource;

import com.example.shopservice.entity.Product;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductResourceTest {

    @Test
    @Order(1)
    public void testCreateProduct() {
        Product product = new Product();
        product.name = "Widget";
        product.price = new BigDecimal("19.99");

        given()
                .contentType(ContentType.JSON)
                .body(product)
                .when()
                .post("/products")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("name", equalTo("Widget"))
                .body("price", equalTo(19.99f));
    }

    @Test
    @Order(2)
    public void testGetProduct() {
        Product product = new Product();
        product.name = "Gadget";
        product.price = new BigDecimal("10.50");

        Long id =
                given()
                        .contentType(ContentType.JSON)
                        .body(product)
                        .when()
                        .post("/products")
                        .then()
                        .extract().jsonPath().getLong("id");

        given()
                .when()
                .get("/products/" + id)
                .then()
                .statusCode(200)
                .body("name", equalTo("Gadget"))
                .body("price", equalTo(10.50f));
    }

    @Test
    @Order(3)
    public void testUpdateProduct() {
        Product product = new Product();
        product.name = "Thing";
        product.price = new BigDecimal("5.00");

        Long id =
                given()
                        .contentType(ContentType.JSON)
                        .body(product)
                        .when()
                        .post("/products")
                        .then()
                        .extract().jsonPath().getLong("id");

        product.name = "Thing Updated";
        product.price = new BigDecimal("7.50");

        given()
                .contentType(ContentType.JSON)
                .body(product)
                .when()
                .put("/products/" + id)
                .then()
                .statusCode(200)
                .body("name", equalTo("Thing Updated"))
                .body("price", equalTo(7.50f));
    }

    @Test
    @Order(4)
    public void testDeleteProduct() {
        Product product = new Product();
        product.name = "ToDelete";
        product.price = new BigDecimal("1.00");

        Long id =
                given()
                        .contentType(ContentType.JSON)
                        .body(product)
                        .when()
                        .post("/products")
                        .then()
                        .extract().jsonPath().getLong("id");

        given()
                .when()
                .delete("/products/" + id)
                .then()
                .statusCode(204);

        given()
                .when()
                .get("/products/" + id)
                .then()
                .statusCode(404)
                .body(is(emptyOrNullString()));
    }
}