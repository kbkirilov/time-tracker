package utils;

import java.io.File;
import java.sql.*;

import static utils.Constants.DB_URL;

public class Debug {
    public static void debugDatabase() {
        System.out.println("DB_URL: " + DB_URL);

        // Check if file exists
        File dbFile = new File("E:/Programming/TimeTracker/timelog.db");
        System.out.println("Database file exists: " + dbFile.exists());
        System.out.println("Database file path: " + dbFile.getAbsolutePath());
        System.out.println("Can read: " + dbFile.canRead());
        System.out.println("Can write: " + dbFile.canWrite());

        // Test connection
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            System.out.println("Database connection successful!");

            // Test if tables exist
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet tables = meta.getTables(null, null, null, new String[]{"TABLE"});
            System.out.println("Tables in database:");
            while (tables.next()) {
                System.out.println("- " + tables.getString("TABLE_NAME"));
            }

        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void testSimpleInsert() {
        String testSQL = "CREATE TABLE IF NOT EXISTS test_entries (id INTEGER PRIMARY KEY, message TEXT, created_at TEXT)";
        String insertSQL = "INSERT INTO test_entries (message, created_at) VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            // Create test table
            try (PreparedStatement createStmt = conn.prepareStatement(testSQL)) {
                createStmt.executeUpdate();
                System.out.println("Test table created/verified");
            }

            // Insert test data
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSQL)) {
                insertStmt.setString(1, "Test entry: " + System.currentTimeMillis());
                insertStmt.setString(2, java.time.LocalDateTime.now().toString());

                int rowsAffected = insertStmt.executeUpdate();
                System.out.println("Rows inserted: " + rowsAffected);
            }

            // Read back the data
            String selectSQL = "SELECT COUNT(*) as count FROM test_entries";
            try (PreparedStatement selectStmt = conn.prepareStatement(selectSQL);
                 ResultSet rs = selectStmt.executeQuery()) {

                if (rs.next()) {
                    System.out.println("Total test entries in database: " + rs.getInt("count"));
                }
            }

        } catch (SQLException e) {
            System.err.println("Test insert failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
