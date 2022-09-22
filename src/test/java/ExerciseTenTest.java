import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExerciseTenTest {
  @Test
  public void testStringLength() {
    int leftLimit = 97;
    int rightLimit = 122;
    int targetStringLength = 50;
    Random random = new Random();

    String generatedString = random.ints(leftLimit, rightLimit + 1)
            .limit(random.nextInt(targetStringLength))
            .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
            .toString();
    System.out.println(generatedString.length());

    assertTrue(generatedString.length() > 15, "The string length is less than 15");
  }
}
