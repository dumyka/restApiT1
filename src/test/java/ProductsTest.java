import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.Response;
import models.ProductModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ProductsTest {

    ProductModel data = new ProductModel();

    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = "http://9b142cdd34e.vps.myjino.ru:49268";
        RestAssured.filters(RequestLoggingFilter.logRequestTo(System.out), ResponseLoggingFilter.logResponseTo(System.out));
    }


    @Test
    public void productsGetTest() {
        Response response = RestAssured.given()
                .get("/products");

        response.then().assertThat().statusCode(200);
    }

    @Test
    public void productsPostTest() {
        data.setName("Hoka One One");
        data.setCategory("Shoes");
        data.setPrice(15.99);
        data.setDiscount(3);
        Response response = RestAssured.given()
                .body(data)
                .post("/products");

        response.then().assertThat().statusCode(201);
    }

    @Test
    public void secondProductsGetTest() {
        Response response = RestAssured.given()
                .get("/products/4");

        response.then().assertThat().statusCode(200);
        Assertions.assertEquals("Clothing", response.jsonPath().getList("category").get(0));
    }

    @Test
    public void productsPutTest() {
        data.setName("Update Hoka One One");
        data.setCategory("Running shoes");
        data.setPrice(19.99);
        data.setDiscount(3);
        Response response = RestAssured.given()
                .body(data)
                .put("/products/2");

        response.then().assertThat().statusCode(200);
    }

    @Test
    public void productsDeleteTest() {
        Response response = RestAssured.given()
                .delete("/products/2");

        response.then().assertThat().statusCode(200);
    }


}
