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
import static ru.learnqa.userAPI.utility.Constants.*;

public class BaseTestCase {
  protected final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
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
    Map<String, String> userData = new HashMap<>();
    userData.put("email", DEFAULT_USER_EMAIL);
    userData.put("password", DEFAULT_USER_PASSWORD);

    Response responseGetAuth = apiCoreRequests.makePostRequest(URL_LOGIN, userData);
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
            .post(URL_USER)
            .jsonPath();
//    responseCreateAuth.prettyPrint();
    return responseCreateAuth.getString("id");
  }



  protected  Map<String, String> createUserAndLogin() {
    Map<String, String> userData = getRegistrationData();
    Map<String, String> userAuthData = new HashMap<>();
    JsonPath responseCreateUser = RestAssured
            .given()
            .body(userData)
            .post(URL_USER)
            .jsonPath();
    String userId = responseCreateUser.getString("id");
    Map<String, String> authData = new HashMap<>();
    authData.put("email", userData.get("email"));
    authData.put("password", userData.get("password"));
    Response responseGetAuth = RestAssured
            .given()
            .body(authData)
            .post(URL_LOGIN)
            .andReturn();
    String authCookie = getCookie(responseGetAuth,"auth_sid");
    String authHeader = getHeader(responseGetAuth, "x-csrf-token");
//    System.out.println("cookie: " + authCookie + " header: " + authHeader);
    userAuthData.put("authCookie", authCookie);
    userAuthData.put("authToken", authHeader);
    userAuthData.put("userId", userId);
    return userAuthData;
  }
}
