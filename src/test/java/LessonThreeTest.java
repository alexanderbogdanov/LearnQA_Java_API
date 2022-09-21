import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.path.json.JsonPath.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LessonThreeTest {
  @Test
  public void testFor200() {
    Response responce = RestAssured.get("https://playground.learnqa.ru/api/map").andReturn();
    assertEquals(200, responce.statusCode(), "Unexpected status code");

  }

  @Test
  public void testFor404() {
    Response responce = RestAssured.get("https://playground.learnqa.ru/api/map2").andReturn();
    assertEquals(404, responce.statusCode(), "Unexpected status code");

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
    String name = "vasya";
    params.put("name", name);

    JsonPath response = RestAssured
            .given()
            .params(params)
            .get("https://playground.learnqa.ru/api/hello").jsonPath();
    String answer = response.get("answer");
    System.out.println(answer);
    assertEquals("Hello, " + name, answer, "The answer is not expected");

  }
}
