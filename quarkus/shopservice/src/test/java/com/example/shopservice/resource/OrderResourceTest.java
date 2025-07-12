package com.example.shopservice.resource;

import com.example.shopservice.entity.Order;
import com.example.shopservice.entity.User;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;

import java.util.UUID;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrderResourceTest {

    Long userId;

    @BeforeEach
    public void setupUser() {
        // Generate a unique username/email for each test to avoid unique constraint violations
        String unique = UUID.randomUUID().toString();
        User user = new User();
        user.username = "orderuser_" + unique;
        user.email = "orderuser_" + unique + "@example.com";
        userId =
                given()
                        .contentType(ContentType.JSON)
                        .body(user)
                        .post("/users")
                        .then()
                        .extract().jsonPath().getLong("id");
    }

    @Test
    @org.junit.jupiter.api.Order(1)
    public void testCreateOrder() {
        Order order = new Order();
        User user = new User();
        user.id = userId;
        order.user = user;

        given()
                .contentType(ContentType.JSON)
                .body(order)
                .when()
                .post("/orders")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("user.id", equalTo(userId.intValue()));
    }

    @Test
    @org.junit.jupiter.api.Order(2)
    public void testGetOrder() {
        Order order = new Order();
        User user = new User();
        user.id = userId;
        order.user = user;

        Long orderId =
                given()
                        .contentType(ContentType.JSON)
                        .body(order)
                        .when()
                        .post("/orders")
                        .then()
                        .extract().jsonPath().getLong("id");

        given()
                .when()
                .get("/orders/" + orderId)
                .then()
                .statusCode(200)
                .body("user.id", equalTo(userId.intValue()));
    }

    @Test
    @org.junit.jupiter.api.Order(3)
    public void testDeleteOrder() {
        Order order = new Order();
        User user = new User();
        user.id = userId;
        order.user = user;

        Long orderId =
                given()
                        .contentType(ContentType.JSON)
                        .body(order)
                        .when()
                        .post("/orders")
                        .then()
                        .extract().jsonPath().getLong("id");

        given()
                .when()
                .delete("/orders/" + orderId)
                .then()
                .statusCode(204);
    }
}