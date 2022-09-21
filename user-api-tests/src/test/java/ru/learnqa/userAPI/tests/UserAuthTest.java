package ru.learnqa.userAPI.tests;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.learnqa.userAPI.lib.Assertions;
import ru.learnqa.userAPI.lib.BaseTestCase;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserAuthTest extends BaseTestCase {

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

    authCookie = getCookie(responseGetAuth,"auth_sid");
    authHeader = getHeader(responseGetAuth, "x-csrf-token");
    userIdOnAuth = getIntFromJson(responseGetAuth,"user_id" );
  }

  @Test
  public void testAuthUser() {

    Response responseCheckAuth = RestAssured
            .given()
            .header("x-csrf-token", authHeader)
            .cookie("auth_sid", authCookie)
            .get(baseUrl + "user/auth")
            .andReturn();

    Assertions.assertJsonByName(responseCheckAuth, "user_id", userIdOnAuth);
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
    Response responseForCheck = spec.get().andReturn();
    Assertions.assertJsonByName(responseForCheck, "user_id", 0);
  }
}

