import io.restassured.RestAssured;
import io.restassured.http.Cookie;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserAuthTest {
  @Test
  public void testAuthUser() {
    Map<String, String> authData = new HashMap<>();
    authData.put("email", "vinkotov@example.com");
    authData.put("password", "1234");

    Response responseGetAuth = RestAssured
            .given()
            .body(authData)
            .post("https://playground.learnqa.ru/api/user/login")
            .andReturn();

    Map<String, String> cookies = responseGetAuth.getCookies();
    Headers headers = responseGetAuth.getHeaders();
    String authCookie = responseGetAuth.getCookie("auth_sid");
    String authHeader = responseGetAuth.getHeader("x-csrf-token");


    int userIdOnAuth = responseGetAuth.jsonPath().getInt("user_id");

    assertEquals(200, responseGetAuth.getStatusCode(), "Unexpected status code");
    assertTrue(cookies.containsKey("auth_sid"), "Response doesn't have 'auth_sid' cookie");
    assertTrue(headers.hasHeaderWithName("x-csrf-token"), "Response doesn't have 'x-csrf-token' header");
    assertTrue(cookies.containsKey("auth_sid"), "Response doesn't have 'auth_sid' cookie");
    assertTrue(userIdOnAuth > 0, "User id should be greater than 0");

    JsonPath responseCheckAuth = RestAssured
            .given()
            .header("x-csrf-token", authHeader)
            .cookie("auth_sid", authCookie)
            .get("https://playground.learnqa.ru/api/user/auth")
            .jsonPath();

    int userIdOnCheck = responseCheckAuth.getInt("user_id");
    assertTrue(userIdOnCheck > 0, "Unexpected user id " + userIdOnCheck);
    assertEquals(userIdOnAuth, userIdOnCheck, "uid from auth request is not equal to uid from check request");
  }

  @ParameterizedTest
  @ValueSource(strings = {"cookie", "headers"})
  public void testNegativeAuthUser(String condition) {
    Map<String, String> authData = new HashMap<>();
    authData.put("email", "vinkotov@example.com");
    authData.put("password", "1234");

    Response responseGetAuth = RestAssured
            .given()
            .body(authData)
            .post("https://playground.learnqa.ru/api/user/login")
            .andReturn();

    Map<String, String> cookies = responseGetAuth.getCookies();
    Headers headers = responseGetAuth.getHeaders();

    RequestSpecification spec = RestAssured
            .given();
    spec.baseUri("https://playground.learnqa.ru/api/user/auth");

    if (condition.equals("cookie")) {
      spec.cookie("auth_sid", cookies.get("auth_sid"));
    } else if (condition.equals("headers")) {
      spec.header("x-csrf-token", headers.get("x-csrf-token"));
    } else {
      throw new IllegalArgumentException("Condition value is: " + condition);

    }
    JsonPath responseForCheck = spec.get().jsonPath();
    assertEquals(0, responseForCheck.getInt("user_id"), "user_id should be 0 for unauthorized request");
  }

}
