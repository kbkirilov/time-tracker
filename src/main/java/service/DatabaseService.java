package service;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import record.TimeEntry;
import record.TimeEstimate;

import static utils.Constants.*;

public class DatabaseService {

    public DatabaseService() {
        createTimeEntriesIfNotExists();
        createTimeEstimatesIfNotExists();
    }

    private void createTimeEntriesIfNotExists() {
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
            conn.setAutoCommit(true);
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("DB init error: " + e.getMessage());
        }
    }

    private void createTimeEstimatesIfNotExists() {
        String sql = """
                CREATE TABLE IF NOT EXISTS time_estimates (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    project_name TEXT,
                    cd1_estimate_hours REAL,
                    cd2_estimate_hours REAL,
                    pf_estimate_hours REAL,
                    total_estimate_hours REAL,
                    created_at TEXT DEFAULT CURRENT_TIMESTAMP
                );
                """;
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            conn.setAutoCommit(true);
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("DB init error: " + e.getMessage());
        }
    }

    public void createTimeEntry(TimeEntry entry, double hours) {
        String sql = """
                    INSERT INTO time_entries(project_name, entry_date, start_time, end_time, worked_hours, created_at)
                                               VALUES (?, ?, ?, ?, ?, ?);
                """;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            conn.setAutoCommit(true);
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

    public void createTimeEstimateEntry(TimeEstimate entry, double totalHours) {
        String sql = """
                INSERT INTO time_estimates(project_name, cd1_estimate_hours, cd2_estimate_hours, pf_estimate_hours, total_estimate_hours, created_at)
                                           VALUES (?, ?, ?, ?, ?, ?);
                """;

        try (Connection conn = DriverManager.getConnection(DB_URL);
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            conn.setAutoCommit(true);
            pstmt.setString(1, entry.projectName());
            pstmt.setDouble(2, entry.cd1EstimateHours());
            pstmt.setDouble(3, entry.cd2EstimateHours());
            pstmt.setDouble(4, entry.pfEstimateHours());
            pstmt.setDouble(5, totalHours);

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
            conn.setAutoCommit(true);
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
            conn.setAutoCommit(true);
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
            conn.setAutoCommit(true);
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
            conn.setAutoCommit(true);

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
            conn.setAutoCommit(true);
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
            conn.setAutoCommit(true);
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
                ORDER BY created_at DESC
                LIMIT 5
                """;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            conn.setAutoCommit(true);

            while (rs.next()) {
                result.add(rs.getString(1));
            }
        } catch (SQLException e) {
            System.out.println("Query error: " + e.getMessage());
        }

        return result;
    }

    public TreeMap<Integer, String> getLastTenProjectNamesWithIds() {
        TreeMap<Integer, String> result = new TreeMap<>();

        String sql = """
                SELECT id, project_name
                FROM time_entries
                ORDER BY created_at DESC
                LIMIT 10
                """;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            conn.setAutoCommit(true);

            while (rs.next()) {
                int id = rs.getInt(1);
                String project_name = rs.getString(2);
                result.put(id, project_name);
            }
        } catch (SQLException e) {
            System.out.println("Query error: " + e.getMessage());
        }

        return result;
    }

    public Map<String, Double> getEntryDetailsById(int id) {
        Map<String, Double> result = new HashMap<>();

        String sql = """
                SELECT project_name, worked_hours
                FROM time_entries
                WHERE id = ?
                """;
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            conn.setAutoCommit(true);
            pstmt.setInt(1, id);

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

    public TimeEntry getTimeEntryById(int id) {
        String sql = """
                SELECT *
                FROM time_entries
                WHERE id = ?
                """;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            conn.setAutoCommit(true);
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new TimeEntry(
                            rs.getString("project_name"),
                            LocalDate.parse(rs.getString("entry_date")),
                            LocalTime.parse(rs.getString("start_time")),
                            LocalTime.parse(rs.getString("end_time"))
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateEntryById(int id, TimeEntry entry, double roundedHours) {
        String sql = """
            UPDATE time_entries
            SET project_name = ?,
                entry_date = ?,
                start_time = ?,
                end_time = ?,
                worked_hours = ?
            WHERE id = ?
            """;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            conn.setAutoCommit(true);
            pstmt.setString(1, entry.projectName());
            pstmt.setString(2, entry.date().toString());
            pstmt.setString(3, entry.start().toString());
            pstmt.setString(4, entry.end().toString());
            pstmt.setDouble(5, roundedHours);
            pstmt.setInt(6, id);

            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Update error: " + e.getMessage());
        }
    }

    public String getLastEntryProjectName() {
        String lastEntry = "";

        String sql = """
                SELECT project_name
                FROM time_entries
                ORDER BY created_at DESC
                LIMIT 1
                """;
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            conn.setAutoCommit(true);

            if (rs.next()) {
                lastEntry = rs.getString("project_name");
            }
        } catch (SQLException e) {
            System.out.println("Query error: " + e.getMessage());
        }

        return lastEntry;
    }

    public String getLastEntryEndTime() {
        String lastEntryEndTime = "";

        String sql = """
                SELECT end_time
                FROM time_entries
                ORDER BY created_at DESC
                LIMIT 1
                """;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            conn.setAutoCommit(true);

            if (rs.next()) {
                lastEntryEndTime = rs.getString("end_time");
            }
        } catch (SQLException e) {
            System.out.println("Query error: " + e.getMessage());
        }

        return lastEntryEndTime;
    }
}
