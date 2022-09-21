import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.path.json.JsonPath.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
  @ValueSource(strings= {"", "John", "Bill"})
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
      String answer = getAuth.jsonPath().getString("user_id");
      String cookie = getAuth.getCookie("auth_sid");
      String header = getAuth.getHeader("x-csrf-token");
      authCookies.put("auth_sid", cookie);
      authHeader.put("x-csrf-token", header);
      System.out.println(answer);
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
      assertEquals(answer, userId, "oops");


//      assertEquals(200, response.statusCode(), "Unexpected status code");

    }


}
