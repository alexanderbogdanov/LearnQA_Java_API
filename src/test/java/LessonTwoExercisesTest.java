import io.restassured.path.json.JsonPath;
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
}
