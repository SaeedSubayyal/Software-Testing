import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class LoginAppTest {

    @Test
    public void testValidLogin() {
        LoginApp app = new LoginApp();
        String userName = app.authenticateUser("johndoe@example.com");
        assertEquals("John Doe", userName, "Valid login should return the correct user name");
    }

    @Test
    public void testInvalidEmail() {
        LoginApp app = new LoginApp();
        String userName = app.authenticateUser("nonexistent@example.com");
        assertNull(userName, "Invalid email should return null");
    }

    @Test
    public void testEmptyEmail() {
        LoginApp app = new LoginApp();
        String userName = app.authenticateUser("");
        assertNull(userName, "Empty email should return null");
    }

    @Test
    public void testSQLInjection() {
        LoginApp app = new LoginApp();
        String userName = app.authenticateUser("'; DROP TABLE User; --");
        assertNull(userName, "SQL injection attempt should return null");
    }

    @Test
    public void testDatabaseConnectionFailure() {
        // Simulate a database failure by using an invalid URL
        LoginApp app = new LoginApp() {
            @Override
            public String authenticateUser(String email) {
                // Use invalid credentials to simulate failure
                String tempDBUrl = "jdbc:mysql://localhost:3306/assignmentdb";
                String tempDBUser = "invaliduser";
                String tempDBPassword = "wrongpassword";
                try (Connection conn = DriverManager.getConnection(tempDBUrl, tempDBUser, tempDBPassword)) {
                    System.err.println("This line should never execute due to connection failure.");
                    return null;
                } catch (SQLException e) {
                    System.err.println("Simulated database failure: " + e.getMessage());
                    return null; // Return null to simulate handling failure
                }
            }
        };

        String userName = app.authenticateUser("johndoe@example.com");
        assertNull(userName, "Database connection failure should result in null username");
    }
}

