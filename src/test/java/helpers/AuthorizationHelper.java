package helpers;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import models.Token;
import models.User;

public class AuthorizationHelper {
    private static String token;
    private static String AUTH_ENDPOINT = "/login";

    public static String authorize(String username, String password) {
        return token = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(new User(username, password))
                .post(AUTH_ENDPOINT)
                .then().assertThat().statusCode(200)
                .extract().as(Token.class).getAccessToken();
    }
}
