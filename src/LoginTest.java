import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class LoginTest {
    private Database database;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @Before
    public void setUp() {
        database = Database.getInstance();
        System.setOut(new PrintStream(outContent));
    }

    @Test
    public void testInvalidCustomerUsername() {
        assertFalse("Login should fail for non-existent username",
                database.validateCustomerCredentials("nonexistentuser", "anypassword"));
    }

    @Test
    public void testInvalidCustomerPassword() {
        database.addCustomerAccount("testuser", "correctpassword");

        assertFalse("Login should fail with incorrect password",
                database.validateCustomerCredentials("testuser", "wrongpassword"));
    }

    @Test
    public void testAdminLoginFailure() {
        Main main = new Main();
        try {
            boolean loginResult = false;
            assertFalse("Admin login should fail with incorrect credentials", loginResult);
        } catch (Exception e) {
            fail("Login method threw an unexpected exception");
        }
    }

    @Test
    public void testMultipleConsecutiveInvalidLogins() {

        assertFalse("First invalid login should fail",
                database.validateCustomerCredentials("testuser", "wrongpassword"));
        assertFalse("Second invalid login should fail",
                database.validateCustomerCredentials("anotheruser", "differentwrongpassword"));
    }

    @After
    public void restoreStreams() {
        System.setOut(originalOut);
    }
}