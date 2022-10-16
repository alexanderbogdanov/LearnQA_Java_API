import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExerciseTwelveTest {
  @Test
  public void testHeadersCheck() {
    Response response = RestAssured
            .given()
            .get("https://playground.learnqa.ru/api/homework_header")
            .andReturn();
    String secretHeaderName = "";
    Headers allHeaders = response.getHeaders();
    // check for custom header
    for (Header header : allHeaders) {
      if (header.getName().startsWith("x-")) {
        secretHeaderName = header.getName();
      }
    }
    System.out.println(secretHeaderName);
    String secretHeaderValue = response.getHeader(secretHeaderName);
    System.out.println(secretHeaderValue);
    assertTrue(allHeaders.hasHeaderWithName("x-secret-homework-header"), "There are no custom headers in the response");
    assertEquals("Some secret value", secretHeaderValue, "The value of the secret header is wrong");

  }
}
