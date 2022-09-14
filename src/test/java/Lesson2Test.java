import io.restassured.RestAssured;
import io.restassured.http.Headers;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class Lesson2Test {
  private final String app = "https://playground.learnqa.ru/api";

  @Test
  @Disabled
  public void testHello() {
    String url = app + "/get_text";
    Response response = RestAssured
            .get(url)
            .andReturn();
    ResponseBody body = response.getBody();
    String bodyAsString = body.asString();
    System.out.println(bodyAsString);
  }

  @Test
  @Disabled
  public void testRestAssured() {
    String url = app + "/hello";
    Map<String, String> params = new HashMap<>();
    params.put("name", "lolkek");
    Response response = given()
            .queryParams(params)
            .get(url)
            .andReturn();
    response.prettyPrint();
  }

  @Test
  @Disabled
  public void testJsonParse() {
    String url = app + "/hello";
    Map<String, String> params = new HashMap<>();
    params.put("name", "lolkek");
    JsonPath response = given()
            .queryParams(params)
            .get(url)
            .jsonPath();
    String name = response.get("answer2");
    if (name == null) {
      System.out.println("The key 'answer2' is absent");
    } else {
      System.out.println(name);
    }
  }

  @Test
  @Disabled
  public void testCheckTypeGet() {
    String url = app + "/check_type";
    Response response = given()
            .queryParam("param1", "value1")
            .queryParam("param2", "value2")
            .get(url)
            .andReturn();
    response.print();
  }

  @Test
  @Disabled
  public void testCheckTypePostRaw() {
    String url = app + "/check_type";
    Response response = given()
//            .header("Content-Type", "application/json")
            .body("param1=value1&param2=value2")
            .post(url)
            .andReturn();
    response.print();
  }

  @Test
  @Disabled
  public void testCheckTypePostJson() {
    String url = app + "/check_type";
    Response response = given()
            .body("{\"param1\":\"value1\",\"param2\":\"value2\"}")
            .post(url)
            .andReturn();
    response.print();
  }

  @Test
  @Disabled
  public void testCheckTypePostMap() {
    String url = app + "/check_type";
    Map<String, Object> body = new HashMap<>();
    body.put("param1", "value1");
    body.put("param2", "value2");

    Response response =
             given()
            .body(body)
            .post(url)
            .andReturn();
    response.print();
  }

  @Test
  @Disabled
  public void testPrintStatusCode() {
    String url = app + "/check_type";
    Response response = RestAssured
            .get(url)
            .andReturn();
    int statusCode = response.statusCode();
    System.out.println(statusCode);
  }

  @Test
  @Disabled
  public void testServerErrorStatusCode() {
    String url = app + "/get_500";
    Response response = RestAssured
            .get(url)
            .andReturn();
    int statusCode = response.statusCode();
    System.out.println(statusCode);
  }

  @Test
  @Disabled
  public void testClientErrorStatusCode() {
    String url = app + "/get_me_404";
    Response response = RestAssured
            .get(url)
            .andReturn();
    int statusCode = response.statusCode();
    System.out.println(statusCode);
  }

  @Test
  @Disabled
  public void testRedirectFollowFalse() {
    String url = app + "/get_303";
    Response response =
             given()
            .redirects()
            .follow(false)
            .when()
            .get(url)
            .andReturn();
    int statusCode = response.statusCode();
    System.out.println(statusCode);
  }

  @Test
  @Disabled
  public void testRedirectFollowTrue() {
    String url = app + "/get_303";
    Response response =
             given()
            .redirects()
            .follow(true)
            .when()
            .get(url)
            .andReturn();
    int statusCode = response.statusCode();
    System.out.println(statusCode);
  }

  @Test
  @Disabled
  public void testShowAllHeadersRequestHeaders() {
    String url = app + "/show_all_headers";
    Map<String, String> headers = new HashMap<>();
    headers.put("myHeader1", "myValue1");
    headers.put("myHeader2", "myValue2");
    Response response =
             given()
            .headers(headers)
            .when()
            .get(url)
            .andReturn();
    response.prettyPrint();
  }

  @Test
  @Disabled
  public void testShowAllHeaders() {
    String url = app + "/show_all_headers";
    Map<String, String> headers = new HashMap<>();
    headers.put("myHeader1", "myValue1");
    headers.put("myHeader2", "myValue2");
    Response response =
             given()
            .headers(headers)
            .when()
            .get(url)
            .andReturn();
    response.prettyPrint();
    Headers responseHeaders = response.getHeaders();
    System.out.println(responseHeaders);
  }

  @Test
  @Disabled
  public void testLocationHeaderFromRedirect() {
    String url = app + "/get_303";
    Response response = RestAssured
            .given()
            .redirects()
            .follow(false)
            .get(url)
            .andReturn();
    response.prettyPrint();
    String locationHeader = response.getHeader("Location");
    System.out.println(locationHeader);
  }

  @Test
  @Disabled
  public void testGetCookies() {
    String url = app + "/get_auth_cookie";
    Map<String, String> data = new HashMap<>();
    data.put("login", "secret_login");
    data.put("password", "secret_pass");
    Response response = RestAssured
            .given()
            .body(data)
            .when()
            .post(url)
            .andReturn();

    System.out.println("\nPretty text:");
    response.prettyPrint();

    System.out.println("\nHeaders:");
    Headers responseHeaders = response.getHeaders();
    System.out.println(responseHeaders);

    System.out.println("\nCookies:");
    Map<String, String> responseCookies = response.getCookies();
    System.out.println(responseCookies);
  }

  @Test
  @Disabled
  public void testGetAuthCookieValueOnly() {
    String url = app + "/get_auth_cookie";
    Map<String, String> data = new HashMap<>();
    data.put("login", "secret_login");
    data.put("password", "secret_pass");
    Response response = RestAssured
            .given()
            .body(data)
            .when()
            .post(url)
            .andReturn();

    String responseCookie = response.getCookie("auth_cookie");
    System.out.println(responseCookie);
  }

  @Test
  @Disabled
  public void testGetAuthCookieValueOnlyWrongCreds() {
    String url = app + "/get_auth_cookie";
    Map<String, String> data = new HashMap<>();
    data.put("login", "secret_login2");
    data.put("password", "secret_pass2");
    Response response = RestAssured
            .given()
            .body(data)
            .when()
            .post(url)
            .andReturn();

    String responseCookie = response.getCookie("auth_cookie");
    System.out.println(responseCookie);
  }

  @Test
  @Disabled
  public void testGetCookiesWrongCreds() {
    String url = app + "/get_auth_cookie";
    Map<String, String> data = new HashMap<>();
    data.put("login", "secret_login2");
    data.put("password", "secret_pass2");
    Response response = RestAssured
            .given()
            .body(data)
            .when()
            .post(url)
            .andReturn();

    System.out.println("\nPretty text:");
    response.prettyPrint();

    System.out.println("\nHeaders:");
    Headers responseHeaders = response.getHeaders();
    System.out.println(responseHeaders);

    System.out.println("\nCookies:");
    Map<String, String> responseCookies = response.getCookies();
    System.out.println(responseCookies);
  }

  @Test
  @Disabled
  public void testGetAuthCookieAndMakeRequestWithIt() {
    String url = app + "/check_auth_cookie";
    Map<String, String> cookies = new HashMap<>();
    cookies.put("auth_cookie", getAuthCookie());
    Response response =
            given()
                    .cookies(cookies)
                    .when()
                    .post(url)
                    .andReturn();
    response.print();
  }

  @Test
  @Disabled
  public void testCheckAuthCookieWithCondition() {
    String url = app + "/check_auth_cookie";
    Map<String, String> cookies = new HashMap<>();
    if (getAuthCookie() != null) {
      cookies.put("auth_cookie", getAuthCookie());
    }
    Response response =
            given()
                    .cookies(cookies)
                    .when()
                    .post(url)
                    .andReturn();
    response.print();
  }

  private String getAuthCookie() {
    String url = app + "/get_auth_cookie";
    Map<String, String> data = new HashMap<>();
    data.put("login", "secret_login");
    data.put("password", "secret_pass");
    return
            given()
                    .body(data)
                    .contentType("application/json")
                    .when()
                    .post(url).getCookie("auth_cookie");
  }
}
