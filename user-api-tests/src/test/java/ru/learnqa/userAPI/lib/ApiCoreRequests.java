package ru.learnqa.userAPI.lib;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.http.Header;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class ApiCoreRequests {
  @Step("Make a GET-request w/o params")
  public Response makeGetRequest(String url) {
    return given()
            .filter(new AllureRestAssured())
            .get(url)
            .andReturn();
  }


  @Step("Make a GET-request with auth token and auth cookie")
  public Response makeGetRequestWithTokenAndCookie(String url, String token, String cookie) {
    return given()
            .filter(new AllureRestAssured())
            .header(new Header("x-csrf-token", token))
            .cookie("auth_sid", cookie)
            .get(url)
            .andReturn();

  }

  @Step("Make a GET-request with only an auth cookie")
  public Response makeGetRequestWithCookie(String url, String cookie) {
    return given()
            .filter(new AllureRestAssured())
            .cookie("auth_sid", cookie)
            .get(url)
            .andReturn();
  }

  @Step("Make a GET-request with a token only")
  public Response makeGetRequestWithToken(String url, String token) {
    return given()
            .filter(new AllureRestAssured())
            .header(new Header("x-csrf-token", token))
            .get(url)
            .andReturn();
  }

  @Step("Make a POST-request")
  public Response makePostRequest(String url, Map<String, String> authData) {
    return given()
            .filter(new AllureRestAssured())
            .body(authData)
            .post (url)
            .andReturn();
  }

  @Step("Make a PUT-request")
  public Response makePutRequestAuthorized(String url, Map<String, String> editData, String token, String cookie)  {
    return given()
            .filter(new AllureRestAssured())
            .header(new Header("x-csrf-token", token))
            .cookie("auth_sid", cookie)
            .body(editData)
            .put(url)
            .andReturn();
  }


  @Step("Make a PUT-request")
  public Response makePutRequestUnauthorized(String url, Map<String, String> editData)  {
    return given()
            .filter(new AllureRestAssured())
            .body(editData)
            .put(url)
            .andReturn();
  }
  @Step("Make a DELETE-request")
  public Response makeDeleteRequest(String url, String token, String cookie) {
    return given()
            .filter(new AllureRestAssured())
            .header(new Header("x-csrf-token", token))
            .cookie("auth_sid", cookie)
            .delete(url)
            .andReturn();
  }



}
