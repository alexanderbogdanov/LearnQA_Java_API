import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static io.restassured.RestAssured.given;

public class LessonTwoExercisesTest {

  @Test
  public void testPrintSecondMessageText() {
    String url = "https://playground.learnqa.ru/api/get_json_homework";
    JsonPath response = given().get(url).jsonPath();
    ArrayList<String> messages = response.getJsonObject("messages.message");
    System.out.println(messages.get(1));
  }

  @Test
  public void testFirstRedirectUrl() {
    String url = "https://playground.learnqa.ru/api/long_redirect";
    Response response = given()
            .redirects()
            .follow(false)
            .when()
            .get(url)
            .andReturn();
    String locationHeader = response.getHeader("Location");
    System.out.println(locationHeader);
  }

  @Test
  public void testLongRedirect() {
    String url = "https://playground.learnqa.ru/api/long_redirect";
    int counter = 0;
    while (true) {
      Response response = given()
              .redirects()
              .follow(false)
              .when()
              .get(url)
              .andReturn();
      String locationHeader = response.getHeader("Location");
      if (locationHeader == null) {
        break;
      }
      url = locationHeader;
      System.out.println(locationHeader);
      counter++;
    }
    System.out.println("Redirects: " + counter);
  }

}
