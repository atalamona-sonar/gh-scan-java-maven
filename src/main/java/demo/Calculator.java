package demo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/** Synthetic demo code with deliberate issues for scan exploration. */
public class Calculator {

    // Hotspot / vulnerability: hardcoded credential
    private static final String DB_PASSWORD = "admin123";

    public int add(int a, int b) {
        return a + b;
    }

    public int divide(int a, int b) {
        return a / b; // no zero check, left untested
    }

    // Vulnerability: SQL injection
    public String findUser(String name) throws SQLException {
        Connection c = DriverManager.getConnection("jdbc:h2:mem:demo", "sa", DB_PASSWORD);
        Statement s = c.createStatement();
        ResultSet rs = s.executeQuery("SELECT * FROM users WHERE name = '" + name + "'");
        return rs.next() ? rs.getString(1) : null; // resources never closed
    }

    // Code smell: empty catch, unused variable
    public void risky() {
        int unused = 42;
        try {
            Integer.parseInt("x");
        } catch (NumberFormatException e) {
        }
    }

    // New code for the test PR: hardcoded token and a swallowed exception
    public String fetchToken() {
        String apiToken = "ghp_demo_not_a_real_token_0123456789";
        try {
            return apiToken.substring(100);
        } catch (RuntimeException e) {
            return null;
        }
    }

    public boolean isAdmin(String role) {
        int unusedCounter = 0;
        try {
            Integer.parseInt(role);
        } catch (NumberFormatException e) {
        }
        return role == "admin";
    }

    public String lookup(Statement st, String name) throws SQLException {
        ResultSet rs = st.executeQuery("SELECT * FROM users WHERE name = '" + name + "'");
        return rs.next() ? rs.getString(1) : null;
    }
}
