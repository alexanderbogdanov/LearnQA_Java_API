package ru.learnqa.userAPI.tests;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import ru.learnqa.userAPI.lib.BaseTestCase;

import java.util.HashMap;
import java.util.Map;

import static ru.learnqa.userAPI.lib.Assertions.assertJsonByName;
import static ru.learnqa.userAPI.lib.DataGenerator.getRegistrationData;

public class UserEditTest extends BaseTestCase {
  @Test
  public void testEditJustCreatedUser() {
// GENERATE USER
    Map<String, String> userData = getRegistrationData();

    JsonPath responseCreateAuth = RestAssured
            .given()
            .body(userData)
            .post("https://playground.learnqa.ru/api/user")
            .jsonPath();

    String userId = responseCreateAuth.getString("id");

    //LOGIN
    Map<String, String> authData = new HashMap<>();
    authData.put("email", userData.get("email"));
    authData.put("password", userData.get("password"));

    Response responseGetAuth = RestAssured
            .given()
            .body(authData)
            .post("https://playground.learnqa.ru/api/user/login")
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
            .put("https://playground.learnqa.ru/api/user/" + userId)
            .andReturn();

    //GET USER DATA AND COMPARE NAME WITH NEW ONE
    Response responseUserData = RestAssured
            .given()
            .header("x-csrf-token", this.getHeader(responseGetAuth, "x-csrf-token"))
            .cookie("auth_sid", this.getCookie(responseGetAuth, "auth_sid"))
            .get("https://playground.learnqa.ru/api/user/" + userId)
            .andReturn();

    assertJsonByName(responseUserData, "firstName", newName);
  }
}
