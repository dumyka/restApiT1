import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import models.CartModel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class CartTest {

    CartModel data = new CartModel();

    private static String token;

    @BeforeAll
    public static void beforeAll() {
        RestAssured.baseURI = "http://9b142cdd34e.vps.myjino.ru:49268";
        RestAssured.filters(RequestLoggingFilter.logRequestTo(System.out), ResponseLoggingFilter.logResponseTo(System.out));

        token = RestAssured.given()
                .contentType(ContentType.JSON)
                .body("{\"username\": \"dima\", \"password\": \"dima123\"}")
                .post("/login")
                .then().assertThat().statusCode(200)
                .extract().body().jsonPath().getString("access_token");
    }

    @Test
    public void cartGetTest() {
        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .get("/cart");
        response.then().statusCode(404);
    }

    @Test
    public void cartPostTest() {
        data.setProduct_id(2);
        data.setQuantity(55);
        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(data)
                .post("/cart");
        response.then().assertThat().statusCode(201);
        String expectedMessage = "Product added to cart successfully";
        assertEquals(expectedMessage, response.getBody().jsonPath().getString("message"));
    }

    @Test
    public void cartDeleteTest() {
        Response response = RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .delete("/cart/2");

        response.then().assertThat().statusCode(200);
        String expectedMessage = "Product removed from cart";
        assertEquals(expectedMessage, response.getBody().jsonPath().getString("message"));
    }
}
