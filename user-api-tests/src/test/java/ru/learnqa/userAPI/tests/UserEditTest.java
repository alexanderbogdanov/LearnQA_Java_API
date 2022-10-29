package ru.learnqa.userAPI.tests;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import ru.learnqa.userAPI.lib.BaseTestCase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static ru.learnqa.userAPI.lib.Assertions.*;
import static ru.learnqa.userAPI.lib.DataGenerator.getRegistrationData;

public class UserEditTest extends BaseTestCase {

  @Test
  public void testEditJustCreatedUser() {
// GENERATE USER
    Map<String, String> userData = getRegistrationData();

    JsonPath responseCreateAuth = RestAssured
            .given()
            .body(userData)
            .post(USER_URL)
            .jsonPath();

    String userId = responseCreateAuth.getString("id");
//    String userId = createUserAndGetId();
    //LOGIN
    Map<String, String> authData = new HashMap<>();
    authData.put("email", userData.get("email"));
    authData.put("password", userData.get("password"));

    Response responseGetAuth = RestAssured
            .given()
            .body(authData)
            .post(LOGIN_URL)
            .andReturn();
    System.out.println(responseGetAuth.asString());

    //EDIT
    String newName = "Changed Name";
    Map<String, String> editData = new HashMap<>();
    editData.put("firstName", newName);

    Response responseEditUser = RestAssured
            .given()
            .header("x-csrf-token", this.getHeader(responseGetAuth, "x-csrf-token"))
            .cookie("auth_sid", this.getCookie(responseGetAuth, "auth_sid"))
            .body(editData)
            .put(USER_URL + userId)
            .andReturn();

    //GET USER DATA AND COMPARE NAME WITH NEW ONE
    Response responseUserData = RestAssured
            .given()
            .header("x-csrf-token", this.getHeader(responseGetAuth, "x-csrf-token"))
            .cookie("auth_sid", this.getCookie(responseGetAuth, "auth_sid"))
            .get("USER_URL" + userId)
            .andReturn();

    assertJsonByName(responseUserData, "firstName", newName);
  }

  @Test
  public void testGetUserDataNotAuthenticated() {
    String userId = "2";
    String newName = "newName";
    Map<String, String> editData = new HashMap<>();
    editData.put("firstName", newName);

    Response responseChangeUser = apiCoreRequests.makePutRequestUnauthorized(USER_URL + userId, editData);
    System.out.println(responseChangeUser.asString());
    System.out.println(responseChangeUser.statusCode());
    assertResponseTextEquals(responseChangeUser, "Auth token not supplied");
    assertResponseStatusCodeEquals(responseChangeUser, 400);

  }

  @Test
  public void testChangeUserAuthorizedAsDifferentUser() {
    String userToEditId = createUserAndGetId();
    System.out.println("user to edit id before login as default: " + userToEditId);
//    ArrayList<String> authData = loginAsDefaultUser();
//    System.out.println("auth data: " + authData);


//    Response getUser = RestAssured
//            .given()
//            .header("x-csrf-token", authData.get(1))
//            .cookie("auth_sid", authData.get(0))
//            .get("https://playground.learnqa.ru/api/user/" + userToEditId)
//            .andReturn();
//    System.out.println("user to edit id: " + userToEditId);
//    System.out.println("user to edit get answer: " + getUser.asString());
// //   apiCoreRequests.makeGetRequestWithTokenAndCookie("https://playground.learnqa.ru/api/user",authData.get(1), authData.get(0));

    Map<String, String> authData = loginAsDefaultUser();
    Response responseUserData = RestAssured
            .given()
            .cookie("auth_sid", authData.get("authCookie"))
            .header("x-csrf-token", authData.get("authHeader"))
            .put(USER_URL + userToEditId)
            .andReturn();
    System.out.println("user id after put: " + userToEditId);
    System.out.println(responseUserData.asString());
    System.out.println(responseUserData.statusCode());

  }

  @Test
  public void testChangeUserAuthorizedAsDifferentUserVersionTwo() {
    //generate userToEdit
//    String userToEditId = createUserAndGetId();
    Map<String, String> userToEditData = getRegistrationData();

    JsonPath responseCreateAuth = RestAssured
            .given()
            .body(userToEditData)
            .post(USER_URL)
            .jsonPath();

    String userToEditId = responseCreateAuth.getString("id");
    System.out.println("user to edit id: " + userToEditId);

    //generate userEditor
    Map<String, String> userEditorData = getRegistrationData();

    JsonPath responseCreateAuthEditor = RestAssured
            .given()
            .body(userEditorData)
            .post(USER_URL)
            .jsonPath();
    System.out.println("userEditor json for creating: ");
    String userEditorId = responseCreateAuthEditor.getString("id");
    System.out.println("user editor id: " + userEditorId);

    //login with userEditor
    Map<String, String> authData = new HashMap<>();
    authData.put("email", userEditorData.get("email"));
    authData.put("password", userEditorData.get("password"));

    Response responseGetAuth = RestAssured
            .given()
            .body(authData)
            .post(LOGIN_URL)
            .andReturn();
    System.out.println("logged in as editor: " + responseGetAuth.asString());

    //edit userToEdit
    String newUserName = "Changed Userame";
    Map<String, String> editData = new HashMap<>();
    editData.put("username", newUserName);

    Response responseEditUser = RestAssured
            .given()
            .header("x-csrf-token", this.getHeader(responseGetAuth, "x-csrf-token"))
            .cookie("auth_sid", this.getCookie(responseGetAuth, "auth_sid"))
            .body(editData)
            .put(USER_URL + userToEditId)
            .andReturn();
    System.out.println("status code for put: " + responseEditUser.statusCode());

    //compare userToEdit before and after (get userToEdit)
    //check if usertoedit data has changed
    Response responseUserToEditData = RestAssured
            .given()
            .get(USER_URL + userToEditId)
            .andReturn();

//    assertJsonByName(responseUserData, "firstName", newName);
    System.out.println("checking if userToEdit username has changed: " + responseUserToEditData.asString());

//check if editor data has changed
    Response responseUserEditorData = RestAssured
            .given()
            .get(USER_URL + userEditorId)
            .andReturn();

//    assertJsonByName(responseUserData, "firstName", newName);
    System.out.println("checking if userEditor username has changed: " + responseUserEditorData.asString());


  }

@Test
public void testChangeUserAuthorizedAsDifferentUserVersion3() {
  //generate userToEdit
  String userToEditId = createUserAndGetId();
  System.out.println("User to edit id: " + userToEditId);
  // createEditor
  ArrayList<String> userEditorData = createUserAndLogin();
  System.out.println("User editor id: " + userEditorData.get(2));
  //edit userToEdit
  String newUserName = "Changed Userame";
  Map<String, String> editData = new HashMap<>();
  editData.put("username", newUserName);

  apiCoreRequests.makePutRequestAuthorized(
          USER_URL + userToEditId,
          editData,
          userEditorData.get(1),
          userEditorData.get(0));
  //check if userToEdit data has changed
  Response responseUserEditedData = RestAssured
          .given()
          .get(USER_URL + userToEditId)
          .andReturn();
  System.out.println("User edited response after put: " + responseUserEditedData.asString());
//  assertJsonByName(responseUserEditedData, "username", newUserName);
//  assertJsonByName(responseUserEditedData, "username", "learnQA");

  //check if userEditor data has changed
  Response responseUserEditorData = RestAssured
          .given()
          .get(USER_URL + userEditorData.get(2))
          .andReturn();
  System.out.println("User editor response after put: " + responseUserEditorData.asString());

//  assertJsonByName(responseUserEditorData, "username", newUserName);

}

@Test
  public void testUpdateUserEmailWithoutAt() {
  ArrayList<String> userData = createUserAndLogin();
  String wrongEmail = "wrongEmail.com";
  Map<String, String> editData = new HashMap<>();
  editData.put("email", wrongEmail);

  Response responseChangeEmail = apiCoreRequests.makePutRequestAuthorized(
          USER_URL + userData.get(2),
          editData,
          userData.get(1),
          userData.get(0));

  assertResponseTextEquals(responseChangeEmail, "Invalid email format");
  assertResponseStatusCodeEquals(responseChangeEmail, 400);

}

  @Test
  public void testChangeFirstNameTooShort() {
    ArrayList<String> userData = createUserAndLogin();
    String shortFirstName = "a";
    Map<String, String> editData = new HashMap<>();
    editData.put("firstName", shortFirstName);

    Response responseChangeFirstName = apiCoreRequests.makePutRequestAuthorized(
            USER_URL + userData.get(2),
            editData,
            userData.get(1),
            userData.get(0));
    System.out.println(responseChangeFirstName.asString());
    System.out.println(responseChangeFirstName.statusCode());

    assertResponseTextEquals(responseChangeFirstName, "{\"error\":\"Too short value for field firstName\"}");
    assertResponseStatusCodeEquals(responseChangeFirstName, 400);

  }


}