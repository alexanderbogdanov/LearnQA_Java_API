package ru.learnqa.userAPI.lib;

import io.restassured.RestAssured;
import io.restassured.http.Headers;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasKey;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.learnqa.userAPI.lib.DataGenerator.getRegistrationData;

public class BaseTestCase {
  protected final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
  protected final String BASE_URL = "https://playground.learnqa.ru/api/";
  protected final int DEFAULT_USER_ID = 2;
  protected final String USER_URL = "https://playground.learnqa.ru/api/user/";
  protected final String LOGIN_URL = "https://playground.learnqa.ru/api/user/login";
  protected final String AUTH_URL = "https://playground.learnqa.ru/api/user/auth";

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


  protected Map<String, String> loginAsDefaultUser() {
    Map<String, String> defaultUserCreds = new HashMap<>();
    defaultUserCreds.put("email", "vinkotov@example.com");
    defaultUserCreds.put("password", "1234");

    Response responseGetAuth = RestAssured
            .given()
            .body(defaultUserCreds)
            .post("https://playground.learnqa.ru/api/user/login")
            .andReturn();
    Map<String, String> authData = new HashMap<>();
    String authCookie = getCookie(responseGetAuth,"auth_sid");
    String authToken = getHeader(responseGetAuth, "x-csrf-token");
    authData.put("authToken", authToken);
    authData.put("authCookie", authCookie);
    System.out.println(responseGetAuth.asString());
    return authData;
  }

  protected String createUserAndGetId() {
    Map<String, String> userData = getRegistrationData();
    JsonPath responseCreateAuth = RestAssured
            .given()
            .body(userData)
            .post("https://playground.learnqa.ru/api/user")
            .jsonPath();
//    responseCreateAuth.prettyPrint();
    return responseCreateAuth.getString("id");
  }

  protected ArrayList<String> createUserAndGetAuthData() {
    Map<String, String> userData = getRegistrationData();
    Response responseCreateAuth = RestAssured
            .given()
            .body(userData)
            .post("https://playground.learnqa.ru/api/user")
            .andReturn();
    String authCookie = getCookie(responseCreateAuth,"auth_sid");
    String authHeader = getHeader(responseCreateAuth, "x-csrf-token");
    System.out.println(responseCreateAuth.asString());
    return new ArrayList<>(
            List.of(authCookie, authHeader));
//    return responseCreateAuth.getString("id");
  }

  protected ArrayList<String> createUserAndLogin() {
    Map<String, String> userData = getRegistrationData();
    JsonPath responseCreateUser = RestAssured
            .given()
            .body(userData)
            .post("https://playground.learnqa.ru/api/user")
            .jsonPath();
    String userId = responseCreateUser.getString("id");
    Map<String, String> authData = new HashMap<>();
    authData.put("email", userData.get("email"));
    authData.put("password", userData.get("password"));
    Response responseGetAuth = RestAssured
            .given()
            .body(authData)
            .post("https://playground.learnqa.ru/api/user/login")
            .andReturn();
    String authCookie = getCookie(responseGetAuth,"auth_sid");
    String authHeader = getHeader(responseGetAuth, "x-csrf-token");
//    System.out.println("cookie: " + authCookie + " header: " + authHeader);
    return new ArrayList<>(
            List.of(authCookie, authHeader, userId));
  }
}
