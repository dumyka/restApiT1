import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserTests {

    private static Faker faker;

    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = "http://9b142cdd34e.vps.myjino.ru:49268";
        RestAssured.filters(RequestLoggingFilter.logRequestTo(System.out), ResponseLoggingFilter.logResponseTo(System.out));
        faker = new Faker();
    }

    @Test
    public void registerTest() {
        String username = faker.name().username();
        String password = faker.internet().password();

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body("{\"username\": \"" + username + "\", \"password\": \"" + password + "\"}")
                .post("/register");

        response.then().statusCode(201);
        String expectedMessage = "User registered successfully";
        assertEquals(expectedMessage, response.getBody().jsonPath().getString("message"));
    }

    @Test
    public void loginTest() {
        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body("{\"username\": \"dima\", \"password\": \"dima123\"}")
                .post("/login");

        response.then().statusCode(200);
    }

}
