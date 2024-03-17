package tests;

import helpers.AuthorizationHelper;
import helpers.CartHelper;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import models.CartModel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import response.CartResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class CartTest {

    private static String token;
    private static int productId = 1;
    private static int quantity = 11;
    private static String username = "dima";
    private static String password = "dima123";
    private static String CART_ENDPOINT = "/cart";


    @BeforeAll
    public static void beforeAll() {
        RestAssured.baseURI = "http://9b142cdd34e.vps.myjino.ru:49268";
        RestAssured.filters(RequestLoggingFilter.logRequestTo(System.out), ResponseLoggingFilter.logResponseTo(System.out));

        token = AuthorizationHelper.authorize(username, password);
    }

    @Test
    @Tag("Positive")
    @DisplayName("Получение информации о всех товарах")
    public void cartGetTest() {
        CartHelper.addProduct(token, productId, quantity);
        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .get(CART_ENDPOINT);
        response.then().statusCode(200);
        response.getBody().as(CartResponse.class);
    }

    @Test
    @Tag("Negative")
    @DisplayName("Получение информации о всех товарах за не авторизованного пользователя")
    public void getProductWithoutAuthorizeTest() {
        CartHelper.addProduct(token, productId, quantity);
        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .get(CART_ENDPOINT);
        response.then().statusCode(401);
        String expectedMessage = "Missing Authorization Header";
        assertEquals(expectedMessage, response.getBody().jsonPath().getString("msg"));
    }

    @Test
    @Tag("Positive")
    @DisplayName("Добавление товара")
    public void cartPostTest() {
        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(new CartModel(1, 20))
                .post(CART_ENDPOINT);
        response.then().assertThat().statusCode(201);
        String expectedMessage = "Product added to cart successfully";
        assertEquals(expectedMessage, response.getBody().jsonPath().getString("message"));
    }

    @Test
    @Tag("Negative")
    @DisplayName("Добавление не существующего товара")
    public void addNonExistentProductTest() {
        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(new CartModel(99, 55))
                .post(CART_ENDPOINT);
        response.then().assertThat().statusCode(404);
        String expectedMessage = "Product not found";
        assertEquals(expectedMessage, response.getBody().jsonPath().getString("message"));
    }

    @Test
    @Tag("Negative")
    @DisplayName("Добавление товара без тела запроса")
    public void addProductWithoutBodyRequestTest() {
        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .post(CART_ENDPOINT);
        response.then().assertThat().statusCode(400);
    }

    @Test
    @Tag("Negative")
    @DisplayName("Добавление товара без обязательного поля")
    public void addProductWithoutRequiredFieldTest() {
        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body("{\"quantity\": " + quantity + "}")
                .post(CART_ENDPOINT);
        response.then().assertThat().statusCode(404);
        String expectedMessage = "Product not found";
        assertEquals(expectedMessage, response.getBody().jsonPath().getString("message"));
    }

    @Test
    @Tag("Negative")
    @DisplayName("Добавление товара не авторизованным пользователем")
    public void addProductWithoutAuthorizeTest() {
        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(new CartModel(2, 55))
                .post(CART_ENDPOINT);
        response.then().assertThat().statusCode(401);
        String expectedMessage = "Missing Authorization Header";
        assertEquals(expectedMessage, response.getBody().jsonPath().getString("msg"));
    }

    @Test
    @Tag("Positive")
    @DisplayName("Удаление товара")
    public void cartDeleteTest() {
        Response response = RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .delete(CART_ENDPOINT + "/2");

        response.then().assertThat().statusCode(200);
        String expectedMessage = "Product removed from cart";
        assertEquals(expectedMessage, response.getBody().jsonPath().getString("message"));
    }

    @Test
    @Tag("Negative")
    @DisplayName("Удаление товара не авторизованным пользователем")
    public void cartDeleteWithoutAuthorizeTest() {
        Response response = RestAssured.given()
                .delete(CART_ENDPOINT + "/2");

        response.then().assertThat().statusCode(401);
        String expectedMessage = "Missing Authorization Header";
        assertEquals(expectedMessage, response.getBody().jsonPath().getString("msg"));
    }

    @Test
    @Tag("Negative")
    @DisplayName("Удаление не существующего товара")
    public void deleteNonExistentProductTest() {
        Response response = RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .delete(CART_ENDPOINT + "/222");

        response.then().assertThat().statusCode(404);
        String expectedMessage = "Product not found in cart";
        assertEquals(expectedMessage, response.getBody().jsonPath().getString("message"));
    }
}
