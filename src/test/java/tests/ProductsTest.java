package tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.Response;
import models.ProductModel;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProductsTest {

    public static final String PATH = "src/test/resources";
    ProductModel data = new ProductModel();
    public static String PRODUCT_ENDPOINT = "/products";

    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = "http://9b142cdd34e.vps.myjino.ru:49268";
        RestAssured.filters(RequestLoggingFilter.logRequestTo(System.out), ResponseLoggingFilter.logResponseTo(System.out));
    }


    @Test
    @Tag("Positive")
    @DisplayName("Получение списка товаров")
    public void productsGetTest() {
        RestAssured.given()
                .get(PRODUCT_ENDPOINT)
                .then().assertThat().statusCode(200)
                // проверка что тело ответа соответствует схеме
                .and().extract().as(new ObjectMapper().getTypeFactory().constructCollectionType(List.class, ProductModel.class));
    }

    @Test
    @Tag("Positive")
    @DisplayName("Проверка файла JSON")
    public void productJsonTest() throws IOException {
        File file = new File(PATH + "/product.json");
        ObjectMapper mapper = new ObjectMapper();
        ProductModel product = mapper.readValue(file, ProductModel.class);
        Assertions.assertEquals(1, product.getId());
        Assertions.assertEquals("HP Pavilion Laptop", product.getName());
        Assertions.assertEquals("Electronics", product.getCategory());
        Assertions.assertEquals(10.99, product.getPrice());
        Assertions.assertEquals(10, product.getDiscount());
    }

    @Test
    @Tag("Positive")
    @DisplayName("Создание товара")
    public void productsPostTest() {
        data.setName("Hoka One One");
        data.setCategory("Shoes");
        data.setPrice(15.99);
        data.setDiscount(3);
        RestAssured.given()
                .body(data)
                .post(PRODUCT_ENDPOINT)
                .then().assertThat().statusCode(201);
    }

    @Test
    @Tag("Positive")
    @DisplayName("Получение информации о товаре по id")
    public void secondProductsGetTest() {
        Response response = RestAssured.given()
                .get(PRODUCT_ENDPOINT + "/4");

        response.then().assertThat().statusCode(200);
        Assertions.assertEquals("Clothing", response.jsonPath().getString("[0].category"));
        Assertions.assertEquals("Levis Jeans", response.jsonPath().getString("[0].name"));
        Assertions.assertEquals(15.0, response.jsonPath().getDouble("[0].discount"));
        Assertions.assertEquals(12.99, response.jsonPath().getDouble("[0].price"));
        Assertions.assertEquals(4, response.jsonPath().getInt("[0].id"));
    }

    @Test
    @Tag("Negative")
    @DisplayName("Получение не существующего товара")
    public void getNonExistentProductTest() {
        Response response = RestAssured.given()
                .get(PRODUCT_ENDPOINT + "/44");

        response.then().assertThat().statusCode(404);
        String expectedMessage = "Product not found";
        assertEquals(expectedMessage, response.getBody().jsonPath().getString("message"));
    }

    @Test
    @Tag("Positive")
    @DisplayName("Редактирование товара")
    public void productsPutTest() {
        data.setName("Update Hoka One One");
        data.setCategory("Running shoes");
        data.setPrice(19.99);
        data.setDiscount(3);
        RestAssured.given()
                .body(data)
                .put(PRODUCT_ENDPOINT + "/2")
                .then().assertThat().statusCode(200);
    }

    @Test
    @Tag("Positive")
    @DisplayName("Удаление товара")
    public void productsDeleteTest() {
        RestAssured.given()
                .delete(PRODUCT_ENDPOINT + "/4")
                .then().assertThat().statusCode(200);
    }


}
