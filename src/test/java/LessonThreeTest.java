import io.restassured.RestAssured;
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


// before user test

public class LessonThreeTest {
  @Test
  public void testFor200() {
    Response response = RestAssured.get("https://playground.learnqa.ru/api/map").andReturn();
    assertEquals(200, response.statusCode(), "Unexpected status code");

  }

  @Test
  public void testFor404() {
    Response response = RestAssured.get("https://playground.learnqa.ru/api/map2").andReturn();
    assertEquals(404, response.statusCode(), "Unexpected status code");

  }

  @Test
  public void testHelloMethodWithoutName() {
    JsonPath response = RestAssured.get("https://playground.learnqa.ru/api/hello").jsonPath();
    String answer = response.get("answer");
    System.out.println(answer);
    assertEquals("Hello, someone", answer, "The answer is not expected");

  }

  @Test
  public void testHelloMethodWithName() {
    Map<String, String> params = new HashMap<>();
    String name = "Benedict Cumberbatch";
    params.put("name", name);

    JsonPath response = RestAssured
            .given()
            .params(params)
            .get("https://playground.learnqa.ru/api/hello").jsonPath();
//    String answer = response.get("answer");
    String answer = response.getString("answer");
    System.out.println(answer);
    assertEquals("Hello, " + name, answer, "The answer is not expected");

  }

  @ParameterizedTest
  @ValueSource(strings = {"", "John", "Bill"})
  public void testHelloMethodName(String name) {
    Map<String, String> params = new HashMap<>();
    if (name.length() > 0) {
      params.put("name", name);
    }

    JsonPath response = RestAssured
            .given()
            .queryParams(params)
            .get("https://playground.learnqa.ru/api/hello").jsonPath();
    String answer = response.getString("answer");
    String expectedName = (name.length() > 0) ? name : "someone";
    System.out.println(answer);
    assertEquals("Hello, " + expectedName, answer, "The answer is not expected");

  }


  @Test
  public void testUserAuth() {
    Map<String, String> creds = new HashMap<>();
    Map<String, String> authCookies = new HashMap<>();
    Map<String, String> authHeader = new HashMap<>();
    creds.put("email", "vinkotov@example.com");
    creds.put("password", "1234");
    Response getAuth = RestAssured
            .given()
            .queryParams(creds)
            .post("https://playground.learnqa.ru/api/user/login")
            .andReturn();
    String authorizedUserId = getAuth.jsonPath().getString("user_id");
    String cookie = getAuth.getCookie("auth_sid");
    String header = getAuth.getHeader("x-csrf-token");
    authCookies.put("auth_sid", cookie);
    authHeader.put("x-csrf-token", header);
    System.out.println(authorizedUserId);
    System.out.println(cookie);
    System.out.println(header);
    Response checkAuth = RestAssured
            .given()
            .cookies(authCookies)
            .headers(authHeader)
            .get("https://playground.learnqa.ru/api/user/auth")
            .andReturn();
    String userId = checkAuth.jsonPath().getString("user_id");
    System.out.println(userId);
    assertEquals(authorizedUserId, userId, "oops");


//      assertEquals(200, response.statusCode(), "Unexpected status code");

  }

  @Test
  public void testAuthUser() {
    Map<String, String> authData = new HashMap<>();
    authData.put("email", "vinkotov@example.com");
    authData.put("password", "1234");

    Response responseGetAuth = RestAssured
            .given()
            .queryParams(authData)
            .post("https://playground.learnqa.ru/api/user/login")
            .andReturn();
    responseGetAuth.prettyPrint();
    Map<String, String> cookies = responseGetAuth.getCookies();
    Headers headers = responseGetAuth.getHeaders();
    int userId = responseGetAuth.jsonPath().getInt("user_id");

    assertEquals(200, responseGetAuth.statusCode(), "Unexpected status code");
    assertTrue(cookies.containsKey("auth_sid"), "Response doesn't have 'auth_sid' cookie");
    assertTrue(headers.hasHeaderWithName("x-csrf-token"), "Response doesn't have 'x-csrf-token' header");
    assertTrue(userId > 0, "User ID should be > 0");

    JsonPath responseCheckAuth = RestAssured
            .given()
            .header("x-csrf-token", responseGetAuth.getHeader("x-csrf-token"))
            .cookies("auth_sid", responseGetAuth.getCookie("auth_sid"))
            .get("https://playground.learnqa.ru/api/user/auth")
            .jsonPath();

    int userIdOnCheck = responseCheckAuth.getInt("user_id");
    assertTrue(userIdOnCheck > 0, "Unexpected user id " + userIdOnCheck);
    assertEquals(userId, userIdOnCheck, "User id from auth request is not equal from user id from auth check request");

  }

  @ParameterizedTest
  @ValueSource(strings = {"cookie", "headers"})
  public void testNegativeAuthUser(String condition) {
    Map<String, String> authData = new HashMap<>();
    authData.put("email", "vinkotov@example.com");
    authData.put("password", "1234");

    Response responseGetAuth = RestAssured
            .given()
            .queryParams(authData)
            .post("https://playground.learnqa.ru/api/user/login")
            .andReturn();

    Map<String, String> cookies = responseGetAuth.getCookies();
    Headers headers = responseGetAuth.getHeaders();

    RequestSpecification spec = RestAssured.given();
    spec.baseUri("https://playground.learnqa.ru/api/user/auth");

    if (condition.equals("cookie")) {
      spec.cookie("auth_sid", cookies.get("auth_sid"));
    } else if (condition.equals("headers")) {
      spec.header("x-csrf-token", headers.get("x-csrf-token"));
    } else {
      throw new IllegalArgumentException("Condition value is unknown " + condition);
    }
    JsonPath responseForCheck = spec.get().jsonPath();
    assertEquals(0, responseForCheck.getInt("user_id"), "uid for unauth user should be 0");

  }


}
