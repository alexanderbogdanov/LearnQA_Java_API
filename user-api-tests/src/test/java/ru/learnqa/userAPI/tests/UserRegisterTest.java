package ru.learnqa.userAPI.tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.learnqa.userAPI.lib.ApiCoreRequests;
import ru.learnqa.userAPI.lib.BaseTestCase;


import java.util.HashMap;
import java.util.Map;

import static ru.learnqa.userAPI.lib.Assertions.*;
import static ru.learnqa.userAPI.lib.DataGenerator.getRegistrationData;

@Epic("User registration tests")
@Feature("Registration")
public class UserRegisterTest extends BaseTestCase {

  @Test
  @Description("Testing for impossibility of creating new user with existing email")
  @DisplayName("Creating user with existing email")
  public void testCreateUserWithExistingEmail() {
    String existingEmail = "vinkotov@example.com";

    Map<String, String> userData = new HashMap<>();
    userData.put("email", existingEmail);
    userData = getRegistrationData(userData);

    Response responseCreateAuth = apiCoreRequests.makePostRequest(USER_URL, userData);
    assertResponseTextEquals(responseCreateAuth, "Users with email '" + existingEmail + "' already exists");
    assertResponseStatusCodeEquals(responseCreateAuth, 400);
  }

  @Test
  @Description("Testing for successful user creation")
  @DisplayName("User creation success")
  public void testCreateUserSuccess() {
    Map<String, String> userData = getRegistrationData();
    Response responseCreateAuth = apiCoreRequests.makePostRequest(USER_URL, userData);
    assertResponseStatusCodeEquals(responseCreateAuth, 200);
    assertJsonHasField(responseCreateAuth, "id");
  }

  @Test
  @Description("Testing for impossibility of creating user with email without @")
  @DisplayName("Email w/o @")
  public void testCreateUserEmailWithoutAt() {
    String wrongEmail = "someEmailExample.com";
    Map<String, String> userData = new HashMap<>();
    userData.put("email", wrongEmail);
    userData = getRegistrationData(userData);

    Response responseCreateAuth = apiCoreRequests
            .makePostRequest(USER_URL, userData);
    assertResponseTextEquals(responseCreateAuth, "Invalid email format");
    assertResponseStatusCodeEquals(responseCreateAuth, 400);
 }

  @Description("Testing for impossibility of creating user without required fields")
  @DisplayName("Creating user w/o required fields")
  @ParameterizedTest
  @ValueSource(strings = {"username", "firstName", "lastName", "email", "password"})
  public void testCreatingWithoutRequiredFields(String key) {

    Map<String, String> userData = getRegistrationData();
    userData.remove(key);

    Response responseCreateAuth = apiCoreRequests
            .makePostRequest(USER_URL, userData);
    assertResponseStatusCodeEquals(responseCreateAuth,400);
    assertResponseTextEquals(responseCreateAuth, "The following required params are missed: " + key);

  }

  @Test
  @Description("Testing for impossibility of creating user with very short name")
  @DisplayName("Name is too short")
  public void testUserNameTooShort() {
    String shortUserName ="x";
    Map<String, String> userData = new HashMap<>();
    userData.put("username", shortUserName);
    userData = getRegistrationData(userData);
    Response responseCreateAuth = apiCoreRequests
            .makePostRequest(USER_URL, userData);
    assertResponseTextEquals(responseCreateAuth, "The value of 'username' field is too short");
    assertResponseStatusCodeEquals(responseCreateAuth, 400);
}


  @Test
  @Description("Testing for impossibility of creating user with very long name")
  @DisplayName("Name is too long")
  public void testUserNameTooLong() {
    String longUserName = StringUtils.repeat("x", 251);
    Map<String, String> userData = new HashMap<>();
    userData.put("username", longUserName);
    userData = getRegistrationData(userData);
    Response responseCreateAuth = apiCoreRequests
            .makePostRequest(USER_URL, userData);
    assertResponseTextEquals(responseCreateAuth, "The value of 'username' field is too long");
    assertResponseStatusCodeEquals(responseCreateAuth, 400);
 }

 //Short & long names but in one test (not so pretty)

  @ParameterizedTest
  @Disabled
  @ValueSource(strings = {"shortName", "longName"})
  public void testNamesOutOfRange(String condition) {
   String shortUserName ="x";
   String longUserName = StringUtils.repeat("x", 251);
   Map<String, String> userData = new HashMap<>();
   if (condition.equals("shortName")) {
     userData.put("username", shortUserName);
     userData = getRegistrationData(userData);
     Response responseCreateAuth = apiCoreRequests.makePostRequest(USER_URL, userData);
     assertResponseTextEquals(responseCreateAuth, "The value of 'username' field is too short");
     assertResponseStatusCodeEquals(responseCreateAuth, 400);

   } else if (condition.equals("longName")) {
     userData.put("username", longUserName);
     userData = getRegistrationData(userData);
     Response responseCreateAuth = apiCoreRequests.makePostRequest(USER_URL, userData);
     assertResponseTextEquals(responseCreateAuth, "The value of 'username' field is too long");
     assertResponseStatusCodeEquals(responseCreateAuth, 400);
   }

 }

}

