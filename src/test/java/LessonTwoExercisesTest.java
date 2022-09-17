import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

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

  @Test
  public void tokensTest() throws InterruptedException {
    String url = "https://playground.learnqa.ru/ajax/api/longtime_job";
    Map<String, String> params = new HashMap<>();
    List<Object> tokenAndTime = getTokenAndTime();
    params.put("token", (String) tokenAndTime.get(1));

    // check the status before the job is ready
    given()
            .queryParams(params)
            .when()
            .get(url)
            .then()
            .assertThat()
            .body("status", equalTo("Job is NOT ready"));

    waitForJob((Integer) tokenAndTime.get(0));

    // check the status after the job is ready
    given()
            .queryParams(params)
            .get(url)
            .then()
            .assertThat()
            .body("status", equalTo("Job is ready"))
            .body("result", notNullValue());
  }

  // create job and get the token
  private List<Object> getTokenAndTime() {
    String url = "https://playground.learnqa.ru/ajax/api/longtime_job";
    JsonPath response =
            given()
                    .contentType("application/json")
                    .when()
                    .get(url)
                    .jsonPath();
    int seconds = response.get("seconds");
    String token = response.get("token");
    return Arrays.asList(seconds, token);
  }

  private void waitForJob(int seconds) throws InterruptedException {
    System.out.println("Waiting for " + (seconds) + " seconds for job to be done");
    TimeUnit.SECONDS.sleep(seconds);
  }
}
