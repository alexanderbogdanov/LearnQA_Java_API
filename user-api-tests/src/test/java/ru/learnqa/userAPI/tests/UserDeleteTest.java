package ru.learnqa.userAPI.tests;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import ru.learnqa.userAPI.lib.BaseTestCase;

import java.util.Map;

import static ru.learnqa.userAPI.lib.Assertions.assertResponseStatusCodeEquals;
import static ru.learnqa.userAPI.lib.Assertions.assertResponseTextEquals;
import static ru.learnqa.userAPI.utility.Constants.*;

public class UserDeleteTest extends BaseTestCase {

  @Test
  public void testDeleteDefaultUser() {
//    ArrayList<String> userData = loginAsDefaultUser();
    Map<String, String> authData = loginAsDefaultUser();
//    Response responseDelete = RestAssured
//            .given()
//            .cookie("auth_sid", userData.get(0))
//            .header("x-csrf-token", userData.get(1))
//            .delete("https://playground.learnqa.ru/api/user/2" )
//            .andReturn();
    Response responseDelete = apiCoreRequests.makeDeleteRequest(
            URL_USER + DEFAULT_USER_ID,
            authData.get("authToken"),
            authData.get("authCookie"));
    assertResponseTextEquals(responseDelete, "Please, do not delete test users with ID 1, 2, 3, 4 or 5.");
    assertResponseStatusCodeEquals(responseDelete, 400);

    System.out.println(responseDelete.asString());
    System.out.println(responseDelete.statusCode());

  }
@Test
  public void testGetDeletedUser() {
  Map<String, String> userToDelete = createUserAndLogin();
//  apiCoreRequests.makeDeleteRequest(
//          URL_USER + userToDeleteData.get("userId"),
//          userToDeleteData.get("authToken"),
//          userToDeleteData.get("authCookie"));
  apiCoreRequests.makeDeleteRequest(userToDelete);
  Response getDeletedUser = apiCoreRequests.makeGetRequestWithoutAuth(URL_USER + userToDelete.get("userId"));
  System.out.println(getDeletedUser.asString());
  System.out.println(getDeletedUser.statusCode());
  assertResponseTextEquals(getDeletedUser, "User not found");
  assertResponseStatusCodeEquals(getDeletedUser, 404);

}

@Test
  public void testDeleteUserAsDifferentUser() {
  //generate userToDelete
  String userToDeleteId = createUserAndGetId();
  System.out.println("User to delete id: " + userToDeleteId);
  // createDeleter
  Map<String, String> userDeleterData = createUserAndLogin();
  System.out.println("User deleter id: " + userDeleterData.get("userId"));
  //delete userToDelete
  apiCoreRequests.makeDeleteRequest(
          URL_USER + userToDeleteId,
          userDeleterData.get("authToken"),
          userDeleterData.get("authCookie"));

  //check if userToDelete was deleted
  Response getDeletedUser = apiCoreRequests.makeGetRequestWithoutAuth(URL_USER + userToDeleteId);
  System.out.println(getDeletedUser.asString());
  System.out.println(getDeletedUser.statusCode());
 //check if deleter was deleted
  Response getDeleter = apiCoreRequests.makeGetRequestWithoutAuth(URL_USER + userDeleterData.get("userId"));
  System.out.println(getDeleter.asString());
  System.out.println(getDeleter.statusCode());

}
}
