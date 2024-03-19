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
        Response response = RestAssured.given()
                .body(data)
                .post(PRODUCT_ENDPOINT);
        response.then().assertThat().statusCode(201);
        ProductModel product = response.getBody().as(ProductModel.class);
        Assertions.assertEquals("Hoka One One", product.getName());
        Assertions.assertEquals("Shoes", product.getCategory());
        Assertions.assertEquals(15.99, product.getPrice());
        Assertions.assertEquals(3, product.getDiscount());
    }

    @Test
    @Tag("Negative")
    @DisplayName("Создание товара c пустым именем")
    public void createProductWithEmptyFieldNameTest() {
        data.setName(" ");
        data.setCategory("Shoes");
        data.setPrice(15.99);
        data.setDiscount(3);
        RestAssured.given()
                .body(data)
                .post(PRODUCT_ENDPOINT)
                .then().assertThat().statusCode(400);
    }

    @Test
    @Tag("Negative")
    @DisplayName("Создание товара без обязательного поля 'name'")
    public void createProductWithOnlyFieldNameTest() {
        data.setCategory("Shoes");
        data.setPrice(15.99);
        data.setDiscount(3);
        RestAssured.given()
                .body(data)
                .post(PRODUCT_ENDPOINT)
                .then().assertThat().statusCode(400);
    }

    @Test
    @Tag("Negative")
    @DisplayName("Создание товара только с полем 'name'")
    public void createProductWithoutRequiredFieldTest() {
        data.setName("Hoka One One");
        RestAssured.given()
                .body(data)
                .post(PRODUCT_ENDPOINT)
                .then().assertThat().statusCode(400);
    }

    @Test
    @Tag("Negative")
    @DisplayName("Создание товара без поля 'price'")
    public void createProductWithoutPriceFieldTest() {
        data.setName("Hoka One One");
        data.setCategory("Shoes");
        data.setDiscount(3);
        RestAssured.given()
                .body(data)
                .post(PRODUCT_ENDPOINT)
                .then().assertThat().statusCode(400);
    }

    @Test
    @Tag("Negative")
    @DisplayName("Создание товара с пустым полем 'price'")
    public void createProductWithoutEmptyFieldPriceTest() {
        data.setName("Hoka One One");
        data.setCategory("Shoes");
        data.setPrice(null);
        data.setDiscount(3);
        RestAssured.given()
                .body(data)
                .post(PRODUCT_ENDPOINT)
                .then().assertThat().statusCode(400);
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
    @Tag("Negative")
    @DisplayName("Частичное обновление товара методом put")
    public void partialUpdateProductTest() {
        data.setName("Update Hoka One One");
        data.setDiscount(3);
        RestAssured.given()
                .body(data)
                .put(PRODUCT_ENDPOINT + "/2")
                .then().assertThat().statusCode(400);
    }

    @Test
    @Tag("Negative")
    @DisplayName("Обновление не существующего товара")
    public void updateNonExistentProductTest() {
        data.setName("Update Hoka One One");
        data.setCategory("Running shoes");
        data.setPrice(19.99);
        data.setDiscount(3);
        RestAssured.given()
                .body(data)
                .put(PRODUCT_ENDPOINT + "/3444")
                .then().assertThat().statusCode(404);
    }

    @Test
    @Tag("Negative")
    @DisplayName("Обновление товара без указания цены")
    public void updateProductWithoutPriceTest() {
        data.setName("Update Hoka One One");
        data.setCategory("Running shoes");
        data.setPrice(null);
        data.setDiscount(3);
        RestAssured.given()
                .body(data)
                .put(PRODUCT_ENDPOINT + "/2")
                .then().assertThat().statusCode(400);
    }

    @Test
    @Tag("Positive")
    @DisplayName("Удаление товара")
    public void productsDeleteTest() {
        RestAssured.given()
                .delete(PRODUCT_ENDPOINT + "/4")
                .then().assertThat().statusCode(200);
    }

    @Test
    @Tag("Negative")
    @DisplayName("Удаление не существующего товара ")
    public void deleteNonExistentProductTest() {
        RestAssured.given()
                .delete(PRODUCT_ENDPOINT + "/400")
                .then().assertThat().statusCode(400);
    }


}
