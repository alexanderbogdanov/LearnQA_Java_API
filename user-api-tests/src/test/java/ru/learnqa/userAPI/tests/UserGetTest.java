package ru.learnqa.userAPI.tests;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import ru.learnqa.userAPI.lib.BaseTestCase;

import java.util.Map;

import static ru.learnqa.userAPI.lib.Assertions.*;
import static ru.learnqa.userAPI.utility.Constants.*;

public class UserGetTest extends BaseTestCase {

  @Test
  public void testGetUserDataNotAuthenticated() {
    Response responseUserData = apiCoreRequests.makeGetRequestWithoutAuth(URL_USER + DEFAULT_USER_ID);

    String[] expectedFields = {"firstName", "lastName", "email"};

    assertJsonHasField(responseUserData, "username");
    assertJsonHasNotFields(responseUserData, expectedFields);
  }

  @Test
  public void testGetUserDataAuthAsSameUser() {
    Map<String, String> authData = loginAsDefaultUser();
    String[] expectedFields = {"username", "firstName", "lastName", "email"};
    Response responseUserData = apiCoreRequests.makeGetRequestWithTokenAndCookie(
            URL_USER + DEFAULT_USER_ID,
            authData.get("authToken"),
            authData.get("authCookie"));
    assertJsonHasFields(responseUserData, expectedFields);
  }
  // Можно было бы создавать юзера и брать его по айдишнику, но я пользуюсь тем, что есть фиксированные юзеры )
  @Test
  public void testGetUserDataAsDifferentUser() {

    Map<String, String> authData = loginAsDefaultUser();
    String[] expectedFields = {"firstName", "lastName", "email"};
    Response responseUserData = apiCoreRequests.makeGetRequestWithTokenAndCookie(
            URL_USER + "1", authData.get("authToken"),
            authData.get("authCookie"));
    assertJsonHasField(responseUserData, "username");
    assertJsonHasNotFields(responseUserData, expectedFields);
  }


}
