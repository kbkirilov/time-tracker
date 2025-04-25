package service;

import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

public class ReportService {
    private static final String DB_URL = "jdbc:sqlite:timelog.db";

    private final DatabaseService db;

    public ReportService(DatabaseService db) {
        this.db = db;
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

    public void reportWeeklyProjectHours() {
        LocalDate start = LocalDate.now().with(DayOfWeek.MONDAY);
        LocalDate end = LocalDate.now().with(DayOfWeek.SUNDAY);
        db.getWeeklyHoursPerProject(start, end);
    }
}
