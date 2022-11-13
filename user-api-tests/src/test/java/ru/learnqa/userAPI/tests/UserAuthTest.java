package ru.learnqa.userAPI.tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.learnqa.userAPI.lib.BaseTestCase;

import java.util.HashMap;
import java.util.Map;

import static ru.learnqa.userAPI.lib.Assertions.assertJsonByName;
import static ru.learnqa.userAPI.utility.Constants.*;


@Epic("Authorization tests")
@Feature("Authorization")
public class UserAuthTest extends BaseTestCase {

  String authCookie;
  String authHeader;
  int userIdOnAuth;

  @BeforeEach

  public void loginDefaultUser() {
    Map<String, String> authData = new HashMap<>();
    authData.put("email", DEFAULT_USER_EMAIL);
    authData.put("password", DEFAULT_USER_PASSWORD);

    Response responseGetAuth = apiCoreRequests
            .makePostRequest(URL_LOGIN, authData);

    authCookie = getCookie(responseGetAuth,"auth_sid");
    authHeader = getHeader(responseGetAuth, "x-csrf-token");
    userIdOnAuth = getIntFromJson(responseGetAuth,"user_id" );
  }

  @Test
  @Description("Successful authorization by email and password")
  @DisplayName("Positive user auth test")
  public void testAuthUser() {

    Response responseCheckAuth = apiCoreRequests
            .makeGetRequestWithTokenAndCookie(URL_AUTH, this.authHeader, this.authCookie);

    assertJsonByName(responseCheckAuth, "user_id", this.userIdOnAuth);
  }

  @Description("Checking authorization without sending auth token or cookie")
  @DisplayName("Negative user auth test")
  @ParameterizedTest
  @ValueSource(strings = {"cookie", "headers"})
  public void testNegativeAuthUser(String condition) {

    if (condition.equals("cookie")) {
      Response responseForCheck = apiCoreRequests.makeGetRequestWithCookie(URL_AUTH, authCookie);
      assertJsonByName(responseForCheck, "user_id", 0);
    } else if (condition.equals("headers")) {
      Response responseForCheck = apiCoreRequests.makeGetRequestWithToken(URL_AUTH, authHeader);
      assertJsonByName(responseForCheck, "user_id", 0);
    } else {
      throw new IllegalArgumentException("Condition value is unknown: " + condition);
    }
  }
}

