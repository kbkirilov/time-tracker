package service;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import record.TimeEntry;

import static utils.Constants.*;

public class DatabaseService {

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

    public void createEntry(TimeEntry entry, double hours) {
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

    public Map<String, Double> getWeeklyWorkedHoursPerProject(LocalDate start, LocalDate end) {
        Map<String, Double> result = new TreeMap<>();
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

            while (rs.next()) {
                String name = rs.getString("project_name");
                double hours = rs.getDouble("total_hours");
                result.put(name, result.getOrDefault(name, 0.00) + hours);
            }
        } catch (SQLException e) {
            System.out.println("Query error: " + e.getMessage());
        }

        return result;
    }

    public Map<String, Double> getWorkedHoursPerProject(String projectName) {
        Map<String, Double> result = new LinkedHashMap<>();

        String sql = """
                SELECT project_name, SUM(worked_hours)
                FROM time_entries
                WHERE project_name LIKE ?
                GROUP BY project_name
            """;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, projectName);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String name = rs.getString("project_name");
                    double hours = rs.getDouble(2);
                    result.put(name, hours);
                }
            }
        } catch (SQLException e) {
            System.out.println("Query error: " + e.getMessage());
        }

        return result;
    }

    public Map<String, Double> getWorkedHoursPerDay() {
        Map<String, Double> result = new LinkedHashMap<>();
        String sql = "SELECT entry_date, SUM(worked_hours) FROM time_entries GROUP BY entry_date";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                result.put(rs.getString(1), rs.getDouble(2));
            }
        } catch (SQLException e) {
            System.out.println("Query error: " + e.getMessage());
        }

        return result;
    }

    public double getWorkedHoursForParticularDay(LocalDate date) {
        String sql = """
                SELECT SUM(worked_hours) as total_hours
                FROM time_entries
                WHERE entry_date = ?
                """;
        double hours = 0;
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, date.toString());
            ResultSet rs = pstmt.executeQuery();

            hours = rs.getDouble("total_hours");
        } catch (SQLException e) {
            System.err.println("Query error: " + e.getMessage());
        }

        return hours;
    }

    public Map<LocalDate, Map<String, Double>> getWeeklyProjectReport(LocalDate start, LocalDate end) {
        Map<LocalDate, Map<String, Double>> weeklyData = new TreeMap<>();

        String sql = """
            SELECT entry_date, project_name, SUM(worked_hours) as total_hours
            FROM time_entries
            WHERE entry_date BETWEEN ? AND ?
            GROUP BY entry_date, project_name
            ORDER BY entry_date, project_name;
            """;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, start.toString());
            pstmt.setString(2, end.toString());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                LocalDate date = LocalDate.parse(rs.getString("entry_date"));
                String projectName = rs.getString("project_name");
                double hours = rs.getDouble("total_hours");

                // Get or create the map for this date
                Map<String, Double> projectHours = weeklyData.getOrDefault(date, new HashMap<>());
                projectHours.put(projectName, hours);
                weeklyData.put(date, projectHours);
            }
        } catch (SQLException e) {
            System.err.println("Query error: " + e.getMessage());
        }

        return weeklyData;
    }

    public List<String> getLastFiveUniqueProjectNames() {
        List<String> result = new ArrayList<>();

        String sql = """
                SELECT DISTINCT project_name
                FROM time_entries
                ORDER BY created_at ASC
                LIMIT 5
                """;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                result.add(rs.getString(1));
            }
        } catch (SQLException e) {
            System.out.println("Query error: " + e.getMessage());
        }

        return result;
    }
}
