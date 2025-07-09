package service;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import record.ProjectAnalysis;
import record.TimeEntry;
import record.TimeEstimate;

import static utils.Constants.*;

public class DatabaseService {
    private static final int CURRENT_SCHEMA_VERSION = 2;

    public DatabaseService() {
        createTimeEntriesIfNotExists();
        createTimeEstimatesIfNotExists();
        migrateDatabase();
    }

    private void migrateDatabase() {
        int currentVersion = getDatabaseVersion();

        if (currentVersion < CURRENT_SCHEMA_VERSION) {
            System.out.println("Migrating database from version " + currentVersion + " to " + CURRENT_SCHEMA_VERSION);

            // Apply migrations in order
            if (currentVersion < 2) {
                migrateToVersion2();
            }

            // Future migrations would go here
            // if (currentVersion < 3) { migrateToVersion3(); }

            updateDatabaseVersion(CURRENT_SCHEMA_VERSION);
            System.out.println("Database migration completed successfully");
        }
    }

    private int getDatabaseVersion() {
        String createVersionTableSql = """
                CREATE TABLE IF NOT EXISTS schema_versions (
                    version INTEGER NOT NULL
                );
                """;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            conn.setAutoCommit(true);
            stmt.execute(createVersionTableSql);

            // Check if version exists
            String getVersionSql = "SELECT version FROM schema_versions LIMIT 1";
            try (ResultSet rs = stmt.executeQuery(getVersionSql)) {
                if (rs.next()) {
                    return rs.getInt("version");
                } else {
                    // First time setup - insert initial version
                    stmt.execute("INSERT INTO schema_versions (version) VALUES (1)");
                    return 1;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting database version: " + e.getMessage());
            return 1; // Default to version 1
        }
    }

    private void migrateToVersion2() {
        String alterTableSql = """
                ALTER TABLE time_entries
                ADD COLUMN project_stages TEXT DEFAULT 'CD1';
                """;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            conn.setAutoCommit(true);
            stmt.execute(alterTableSql);

            System.out.println("Added project_stages column to time_entries table");

        } catch (SQLException e) {
            System.err.println("Error migrating to version 2: " + e.getMessage());
            throw new RuntimeException("Database migration failed", e);
        }
    }

    private void updateDatabaseVersion(int version) {
        String updateVersionSql = "UPDATE schema_versionS SET version = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(updateVersionSql)) {

            conn.setAutoCommit(true);
            pstmt.setInt(1, version);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error updating database version: " + e.getMessage());
            throw new RuntimeException("Failed to update database version", e);
        }
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
                    INSERT INTO time_entries(project_name, entry_date, start_time, end_time, worked_hours, created_at, project_stages)
                                               VALUES (?, ?, ?, ?, ?, ?, ?);
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
            pstmt.setString(7, entry.projectStage());

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

    public void deleteTimeEntryById(int id) {
        String sql = "DELETE FROM time_entries WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            conn.setAutoCommit(true);
            pstmt.setInt(1, id);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected <= 0) {
                System.out.println("No entry found with ID " + id);
            }
        } catch (SQLException e) {
            System.out.println("Delete error: " + e.getMessage());
        }
    }

    public void deleteTimeEstimateEntryById(int id) {
        String sql = "DELETE FROM time_estimates WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            conn.setAutoCommit(true);
            pstmt.setInt(1, id);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected <= 0) {
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

    public TreeMap<Integer, String> getLastTenTimeEntriesProjectNamesWithIds() {
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

    public TreeMap<Integer, String> getLastTenTimeEstimatesProjectNamesWithId() {
        TreeMap<Integer, String> result = new TreeMap<>();

        String sql = """
                SELECT id, project_name
                FROM time_estimates
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

    public Map<String, Double> getTimeEntryDetailsById(int id) {
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

    public Map<String, Double> getTimeEstimatesEntryDetailsById(int id) {
        Map<String, Double> result = new HashMap<>();

        String sql = """
                SELECT project_name, total_estimate_hours
                FROM time_estimates
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
                            rs.getString("project_stages"),
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

    public TimeEstimate getTimeEstimateById(int id) {
        String sql = """
                SELECT *
                FROM time_estimates
                WHERE id = ?
                """;
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            conn.setAutoCommit(true);
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new TimeEstimate(
                            rs.getString("project_name"),
                            rs.getDouble("cd1_estimate_hours"),
                            rs.getDouble("cd2_estimate_hours"),
                            rs.getDouble("pf_estimate_hours")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateTimeEntryById(int id, TimeEntry entry, double roundedHours) {
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

    public void updateTimeEstimateById(int id, TimeEstimate entry, double totalHours) {
        String sql = """
                UPDATE time_estimates
                SET project_name = ?,
                    cd1_estimate_hours = ?,
                    cd2_estimate_hours = ?,
                    pf_estimate_hours = ?,
                    total_estimate_hours = ?
                WHERE id = ?
                """;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            conn.setAutoCommit(true);
            pstmt.setString(1, entry.projectName());
            pstmt.setDouble(2, entry.cd1EstimateHours());
            pstmt.setDouble(3, entry.cd2EstimateHours());
            pstmt.setDouble(4, entry.pfEstimateHours());
            pstmt.setDouble(5, totalHours);
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

    public Map<String, Double> getActualHoursByProjectStage(String projectName) {
        Map<String, Double> result = new HashMap<>();

        String sql = """
                SELECT project_stages, SUM(worked_hours) as total_hours
                FROM time_entries
                WHERE project_name = ?
                GROUP BY project_stages
                """;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            conn.setAutoCommit(true);
            pstmt.setString(1, projectName);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String stage = rs.getString("project_stages");
                    double hours = rs.getDouble("total_hours");
                    result.put(stage, hours);
                }
            }
        } catch (SQLException e) {
            System.err.println("Query error: " + e.getMessage());
        }

        return result;
    }

    /**
     * Get estimated hours for a specific project
     *
     * @param projectName the project name
     * @return TimeEstimate object with all estimate data, or null if not found
     */
    public TimeEstimate getTimeEstimateByProjectName(String projectName) {
        String sql = """
                SELECT *
                FROM time_estimates
                WHERE project_name = ?
                ORDER BY created_at DESC
                LIMIT 1
                """;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            conn.setAutoCommit(true);
            pstmt.setString(1, projectName);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new TimeEstimate(
                            rs.getString("project_name"),
                            rs.getDouble("cd1_estimate_hours"),
                            rs.getDouble("cd2_estimate_hours"),
                            rs.getDouble("pf_estimate_hours")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Query error: " + e.getMessage());
        }

        return null;
    }

    /**
     * Compare actual vs estimated hours for a project
     *
     * @param projectName the project name to analyze
     * @return ProjectAnalysis object with comparison data
     */
    public ProjectAnalysis getProjectAnalysis(String projectName) {
        Map<String, Double> actualHours = getActualHoursByProjectStage(projectName);
        TimeEstimate estimates = getTimeEstimateByProjectName(projectName);

        if (estimates == null) {
            System.out.println("No estimates found for project: " + projectName);
            return null;
        }

        return new ProjectAnalysis(
                projectName,
                actualHours.getOrDefault("CD1", 0.0),
                actualHours.getOrDefault("CD2", 0.0),
                actualHours.getOrDefault("PF", 0.0),
                estimates.cd1EstimateHours(),
                estimates.cd2EstimateHours(),
                estimates.pfEstimateHours()
        );
    }

    public TreeMap<String, ProjectAnalysis> getLastFiveProjectComparison() {
        TreeMap<String, ProjectAnalysis> result = new TreeMap<>();

        List<String> last5UniqueProjectNames = getLastFiveUniqueProjectNames();

        for (int i = 0; i < last5UniqueProjectNames.size(); i++) {
            String currProjectName = last5UniqueProjectNames.get(i);
            ProjectAnalysis currProjectAnalysis = getProjectAnalysis(currProjectName);

            result.put(currProjectName, currProjectAnalysis);
        }

        return result;
    }
}
