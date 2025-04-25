package service;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import record.TimeEntry;

public class DatabaseService {
    private static final String DB_URL = "jdbc:sqlite:timelog.db";

    public DatabaseService() {
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        String sql = """
                CREATE TABLE IF NOT EXISTS time_entries (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    project_name TEXT,
                    entry_date TEXT,
                    start_time TEXT,
                    end_time TEXT,
                    worked_hours REAL,
                    created_at TEXT DEFAULT CURRENT_TIMESTAMP
                );
                """;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("DB init error: " + e.getMessage());
        }
    }

    public void insert(TimeEntry entry, double hours) {
        String sql = """
                    INSERT INTO time_entries(project_name, entry_date, start_time, end_time, worked_hours, created_at)
                                               VALUES (?, ?, ?, ?, ?, ?);
                """;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, entry.projectName());
            pstmt.setString(2, entry.date().toString());
            pstmt.setString(3, entry.start().toString());
            pstmt.setString(4, entry.end().toString());
            pstmt.setDouble(5, hours);

            // Добавяме локален timestamp
            String timestamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            pstmt.setString(6, timestamp);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Insert error: " + e.getMessage());
        }
    }


    public void deleteEntryById(int id) {
        String sql = "DELETE FROM time_entries WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Entry with ID " + id + " deleted successfully.");
            } else {
                System.out.println("No entry found with ID " + id);
            }
        } catch (SQLException e) {
            System.out.println("Delete error: " + e.getMessage());
        }
    }

    public void getWeeklyHoursPerProject(LocalDate start, LocalDate end) {
        String sql = """
                SELECT project_name, SUM(worked_hours) as total_hours
                FROM time_entries
                WHERE entry_date BETWEEN ? AND ?
                GROUP BY project_name
                ORDER BY total_hours DESC;
                """;
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, start.toString());
            pstmt.setString(2, end.toString());
            ResultSet rs = pstmt.executeQuery();

            System.out.printf("Time period of the report: %s --- %s%n", start, end);
            System.out.printf("%-50s | %-10s%n", "Project Name", "Total Hours");
            System.out.println("-".repeat(65));
            while (rs.next()) {
                String name = rs.getString("project_name");
                double hours = rs.getDouble("total_hours");
                System.out.printf("%-50s | %-10.2f%n", name, hours);
            }
        } catch (SQLException e) {
            System.out.println("Query error: " + e.getMessage());
        }
    }
}
