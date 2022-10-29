package ru.learnqa.userAPI.tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import ru.learnqa.userAPI.lib.BaseTestCase;

import java.util.Map;

import static ru.learnqa.userAPI.lib.Assertions.*;

public class UserGetTest extends BaseTestCase {

  @Test
  public void testGetUserDataNotAuthenticated() {
    Response responseUserData = apiCoreRequests.makeGetRequest(USER_URL + DEFAULT_USER_ID);

    String[] expectedFields = {"firstName", "lastName", "email"};

    assertJsonHasField(responseUserData, "username");
    assertJsonHasNotFields(responseUserData, expectedFields);
  }

  @Test
  public void testGetUserDataAuthAsSameUser() {
    Map<String, String> authData = loginAsDefaultUser();

    String[] expectedFields = {"username", "firstName", "lastName", "email"};

    Response responseUserData = RestAssured
            .given()
            .cookie("auth_sid", authData.get("authCookie"))
            .header("x-csrf-token", authData.get("authToken"))
            .get(USER_URL + DEFAULT_USER_ID)
            .andReturn();

    assertJsonHasFields(responseUserData, expectedFields);
  }


  // Можно было бы создавать юзера и брать его по айдишнику, но я пользуюсь тем, что есть фиксированные юзеры )
  @Test
  public void testGetUserDataAsDifferentUser() {

//    ArrayList<String> authData = loginAsDefaultUser();
    Map<String, String> authData = loginAsDefaultUser();

    String[] expectedFields = {"firstName", "lastName", "email"};

    Response responseUserData = RestAssured
            .given()
//            .cookie("auth_sid", authData.get(0))
            .cookie("auth_sid", authData.get("authCookie"))
//            .header("x-csrf-token", authData.get(1))            .cookie("auth_sid", authData.get(0))
            .header("x-csrf-token", authData.get("authToken"))
            .get(USER_URL + "1")
            .andReturn();

    assertJsonHasField(responseUserData, "username");
    assertJsonHasNotFields(responseUserData, expectedFields);
  }


}
