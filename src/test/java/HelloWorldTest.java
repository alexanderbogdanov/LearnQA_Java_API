import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import org.junit.jupiter.api.Test;

public class HelloWorldTest {
  @Test
  public void testHelloWorld() {
    Response response = RestAssured
            .get("https://playground.learnqa.ru/api/get_text")
            .andReturn();
    ResponseBody body = response.getBody();
    String bodyAsString = body.asString();
    System.out.println(bodyAsString);
  }
}
