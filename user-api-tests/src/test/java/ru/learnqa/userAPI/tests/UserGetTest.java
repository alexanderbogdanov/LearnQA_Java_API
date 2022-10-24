package ru.learnqa.userAPI.tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import ru.learnqa.userAPI.lib.BaseTestCase;

import java.util.HashMap;
import java.util.Map;

import static ru.learnqa.userAPI.lib.Assertions.*;

public class UserGetTest extends BaseTestCase {
  @Test
  public void testGetUserDataNotAuthenticated() {
    Response responseUserData = RestAssured
            .get("https://playground.learnqa.ru/api/user/2")
            .andReturn();
    assertJsonHasField(responseUserData, "username");
    assertJsonHasNotField(responseUserData, "firstName");
    assertJsonHasNotField(responseUserData, "lastName");
    assertJsonHasNotField(responseUserData, "email");
  }

  @Test
  public void testGetUserDataAuthAsSameUser() {
    Map<String, String> authData = new HashMap<>();
    authData.put("email", "vinkotov@example.com");
    authData.put("password", "1234");

    Response responseGetAuth = RestAssured
            .given()
            .body(authData)
            .post("https://playground.learnqa.ru/api/user/login")
            .andReturn();

    String authCookie = getCookie(responseGetAuth,"auth_sid");
    String authHeader = getHeader(responseGetAuth, "x-csrf-token");

    Response responseUserData = RestAssured
            .given()
            .header("x-csrf-token", authHeader)
            .cookie("auth_sid", authCookie)
            .get("https://playground.learnqa.ru/api/user/2")
            .andReturn();

    String[] expectedFields = {"username", "firstName", "lastName", "email"};
    assertJsonHasFields(responseUserData, expectedFields);


  }


}
