package ru.learnqa.userAPI.lessons.lib;

import io.restassured.RestAssured;
import io.restassured.http.Headers;
import io.restassured.response.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasKey;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.learnqa.userAPI.lessons.lib.DataGenerator.getRegistrationData;

public class BaseTestCase {
  protected String getHeader(Response Response, String name) {
    Headers headers = Response.getHeaders();

    assertTrue(headers.hasHeaderWithName(name), "Response doesn't have header with name " + name);
    return headers.getValue(name);
  }

  protected String getCookie(Response Response, String name) {
    Map<String, String> cookies = Response.getCookies();

    assertTrue(cookies.containsKey(name), "Response doesn't have cookie with name " + name);
    return cookies.get(name);
  }

  protected int getIntFromJson(Response Response, String name) {
    Response.then().assertThat().body("$", hasKey(name));
    return Response.jsonPath().getInt(name);
  }

  protected ArrayList<String> createUserAndGetAuthData() {
    Map<String, String> userData = getRegistrationData();
    Response responseCreateAuth = RestAssured
            .given()
            .body(userData)
            .post("https://playground.learnqa.ru/api/user/")
            .andReturn();
    String authCookie = getCookie(responseCreateAuth,"auth_sid");
    String authHeader = getHeader(responseCreateAuth, "x-csrf-token");
    System.out.println(responseCreateAuth.asString());
    return new ArrayList<>(
            List.of(authCookie, authHeader));
//    return responseCreateAuth.getString("id");
  }
}
