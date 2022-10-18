package ru.learnqa.userAPI.tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import ru.learnqa.userAPI.lib.Assertions;
import ru.learnqa.userAPI.lib.BaseTestCase;


import java.util.HashMap;
import java.util.Map;

import static ru.learnqa.userAPI.lib.Assertions.*;
import static ru.learnqa.userAPI.lib.DataGenerator.getRandomEmail;

public class UserRegisterTest extends BaseTestCase {

  @Test
  public void testCreateUserWithExistingEmail() {
    String email = "vinkotov@example.com";

    Map<String, String> userData = new HashMap<>();
    userData.put("email", email);
    userData.put("password", "123");
    userData.put("username", "learnQA");
    userData.put("firstName", "learnQA");
    userData.put("lastName", "learnQA");

    Response responseCreateAuth = RestAssured
            .given()
            .body(userData)
            .post("https://playground.learnqa.ru/api/user")
            .andReturn();

    assertResponseTextEquals(responseCreateAuth, "Users with email '" + email + "' already exists");
    assertResponseStatusCodeEquals(responseCreateAuth, 400);
  }

  @Test
  public void testCreateUserSuccess() {
    String email = getRandomEmail();

    Map<String, String> userData = new HashMap<>();
    userData.put("email", email);
    userData.put("password", "123");
    userData.put("username", "learnQA");
    userData.put("firstName", "learnQA");
    userData.put("lastName", "learnQA");

    Response responseCreateAuth = RestAssured
            .given()
            .body(userData)
            .post("https://playground.learnqa.ru/api/user")
            .andReturn();
    assertResponseStatusCodeEquals(responseCreateAuth, 200);
    assertJsonHasKey(responseCreateAuth, "id");
  }



}

