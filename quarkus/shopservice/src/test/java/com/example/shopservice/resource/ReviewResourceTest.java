package com.example.shopservice.resource;

import com.example.shopservice.entity.Product;
import com.example.shopservice.entity.Review;
import com.example.shopservice.entity.User;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.util.UUID;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ReviewResourceTest {

    static Long userId;
    static Long productId;

    @BeforeEach
    public void setup() {
        String unique = UUID.randomUUID().toString();
        User user = new User();
        user.username = "reviewuser_" + unique;
        user.email = "reviewuser_" + unique + "@example.com";
        userId =
                given().contentType(ContentType.JSON).body(user)
                        .when().post("/users")
                        .then().extract().jsonPath().getLong("id");

        Product product = new Product();
        product.name = "reviewprod_" + unique;
        product.price = new BigDecimal("8.90");
        productId =
                given().contentType(ContentType.JSON).body(product)
                        .when().post("/products")
                        .then().extract().jsonPath().getLong("id");
    }

    @Test
    @Order(1)
    public void testCreateReview() {
        Review review = new Review();
        User user = new User();
        user.id = userId;
        review.user = user;
        Product product = new Product();
        product.id = productId;
        review.product = product;
        review.rating = 5;
        review.comment = "Excellent!";

        given()
                .contentType(ContentType.JSON)
                .body(review)
                .when()
                .post("/reviews")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("user.id", equalTo(userId.intValue()))
                .body("product.id", equalTo(productId.intValue()))
                .body("rating", equalTo(5))
                .body("comment", equalTo("Excellent!"));
    }

    @Test
    @Order(2)
    public void testGetReview() {
        Review review = new Review();
        User user = new User();
        user.id = userId;
        review.user = user;
        Product product = new Product();
        product.id = productId;
        review.product = product;
        review.rating = 4;
        review.comment = "Good!";

        Long reviewId =
                given().contentType(ContentType.JSON).body(review)
                        .when().post("/reviews")
                        .then().extract().jsonPath().getLong("id");

        given()
                .when()
                .get("/reviews/" + reviewId)
                .then()
                .statusCode(200)
                .body("rating", equalTo(4))
                .body("comment", equalTo("Good!"));
    }

    @Test
    @Order(3)
    public void testDeleteReview() {
        Review review = new Review();
        User user = new User();
        user.id = userId;
        review.user = user;
        Product product = new Product();
        product.id = productId;
        review.product = product;
        review.rating = 2;
        review.comment = "Not great.";

        Long reviewId =
                given().contentType(ContentType.JSON).body(review)
                        .when().post("/reviews")
                        .then().extract().jsonPath().getLong("id");

        given()
                .when()
                .delete("/reviews/" + reviewId)
                .then()
                .statusCode(204);
    }
}