package fi.sisu;

import java.io.IOException;
import java.util.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * A JUnit test class for the Authentication class. This class tests the login
 * and registration functionality of the Authentication class.
 */
public class AuthenticationTest {

    private UserReaderWriter readerWriter;
    private Authentication auth;
    private String validStudentNumber;

    /**
     * SetUp is annotated with @BeforeEach and is executed before each test
     * method in the class. Sets up the necessary objects and data for the tests
     * to run. If an exception occurs while writing the user file, the test
     * fails with an error message.
     */
    @BeforeEach
    public void setUp() {
        auth = new Authentication();
        validStudentNumber = "12345678";
        User validUser = new User("John Doe", validStudentNumber);
        UserReaderWriter writer = new UserReaderWriter(validUser);
        try {
            writer.writeToFile(validStudentNumber + ".json");
        } catch (Exception e) {
            fail("Unexpected exception thrown while writing user file.");
        }
    }

    /**
     * Test case to verify successful login with valid student number.
     */
    @Test
    public void testLoginSuccess() {
        boolean loginSuccess = auth.login(validStudentNumber);
        assertEquals(validStudentNumber, auth.getCurrentlyLoggedInStudentNumber());

    }

    /**
     * Test case to verify unsuccessful login with valid student number.
     */
    @Test
    public void testLoginFailure() {
        String invalidStudentNumber = "00000000";
        boolean loginSuccess = auth.login(invalidStudentNumber);
        assertEquals("Opiskelijanumero on väärin tai sitä ei löytynyt!", auth.getErrorCause());
    }

    /**
     * Test case to verify successful and unsuccessful registering a new user.
     */
    @Test
    public void testIsRegisterInfoValid() throws IOException {
        // Test valid registration info
        String studentNumber = String.valueOf(new Random().nextInt(900000) + 100000);
        String studentName = "New Student";
        boolean result = auth.isRegisterInfoValid(studentNumber, studentName);
        assertTrue(result);

        // Test empty student number
        studentNumber = "";
        studentName = "Jane Doe";
        result = auth.isRegisterInfoValid(studentNumber, studentName);
        assertFalse(result);
        assertEquals("Opiskelijanumero puuttuu!", auth.getErrorCause());

        // Test empty student name
        studentNumber = "7654321";
        studentName = "";
        result = auth.isRegisterInfoValid(studentNumber, studentName);
        assertFalse(result);
        assertEquals("Nimi puuttuu!", auth.getErrorCause());

        // Test already registered student number
        studentNumber = "1234567";
        studentName = "Another John";
        result = auth.isRegisterInfoValid(studentNumber, studentName);
        assertFalse(result);
        assertEquals("Opiskelijanumero on jo rekisteröity!", auth.getErrorCause());
    }

}
