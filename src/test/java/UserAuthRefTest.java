import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserAuthRefTest {

  String authCookie;
  String authHeader;
  int userIdOnAuth;
  String baseUrl = "https://playground.learnqa.ru/api/";

  @BeforeEach
  public void loginUser() {
    Map<String, String> authData = new HashMap<>();
    authData.put("email", "vinkotov@example.com");
    authData.put("password", "1234");

    Response responseGetAuth = RestAssured
            .given()
            .body(authData)
            .post(baseUrl + "user/login")
            .andReturn();

    authCookie = responseGetAuth.getCookie("auth_sid");
    authHeader = responseGetAuth.getHeader("x-csrf-token");
    userIdOnAuth = responseGetAuth.jsonPath().getInt("user_id");
  }

  @Test
  public void testAuthUser() {

    JsonPath responseCheckAuth = RestAssured
            .given()
            .header("x-csrf-token", authHeader)
            .cookie("auth_sid", authCookie)
            .get(baseUrl + "user/auth")
            .jsonPath();

    int userIdOnCheck = responseCheckAuth.getInt("user_id");
    assertTrue(userIdOnCheck > 0, "Unexpected user id " + userIdOnCheck);
    assertEquals(userIdOnAuth, userIdOnCheck, "uid from auth request is not equal to uid from check request");
  }

  @ParameterizedTest
  @ValueSource(strings = {"cookie", "headers"})
  public void testNegativeAuthUser(String condition) {
 RequestSpecification spec = RestAssured
            .given();
    spec.baseUri(baseUrl + "user/auth");

    if (condition.equals("cookie")) {
      spec.cookie("auth_sid", authCookie);
    } else if (condition.equals("headers")) {
      spec.header("x-csrf-token", authHeader);
    } else {
      throw new IllegalArgumentException("Condition value is: " + condition);

    }
    JsonPath responseForCheck = spec.get().jsonPath();
    assertEquals(0, responseForCheck.getInt("user_id"), "user_id should be 0 for unauthorized request");
  }
}
