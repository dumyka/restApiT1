package helpers;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import models.CartModel;

public class CartHelper {
    public static Response addProduct(String token, int productId, int quantity){
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(new CartModel(productId, quantity))
                .post("/cart")
                .then().assertThat().statusCode(201)
                .extract().response();
    }
}
