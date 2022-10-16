import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ExerciseElevenTest {
  @Test
  public void testCookiesCheck() {
    Response response = RestAssured
            .given()
            .get("https://playground.learnqa.ru/api/homework_cookie")
            .andReturn();
    Map<String, String> cookies = response.getCookies();
    String cookieValue = response.getCookie("HomeWork");
    assertTrue(cookies.containsKey("HomeWork"), "Response doesn't have a cookie 'HomeWork'");
    assertEquals("hw_value", cookieValue, "The value of a cookie 'HomeWork' is wrong");

  }




}
