package service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class LogReaderService {
    public static final String FILE_NAME = "timelog.txt";
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public Map<LocalDate, Double> getTotalHoursPerDay() {
        Map<LocalDate, Double> result = new TreeMap<>();

        try {
            List<String> lines = getListOfLines(FILE_NAME);
            for (String line : lines) {
                // Format: yyyy-MM-dd | projectName | HH:mm - HH:mm | X.XX hours
                String[] parts = line.split("\\|");
                if (parts.length < 4) continue;

                LocalDate date = LocalDate.parse(parts[0].trim(), dateFormatter);
                double hours = Double.parseDouble(parts[3].trim().split(" ")[0]);

                result.put(date, result.getOrDefault(date, 0.0) + hours);
            }
        } catch (IOException e) {
            System.out.println("Error reading log: " + e.getMessage());
        }

        return result;
    }

    /**
     *
     * @param projectName The name under which the project has been tracked in the timelog.txt
     * @return Map<ProjectName, TotalHoursWorked>
     */
    public Map<String, Double> getTotalHoursPerProject(String projectName) {
        Map<String, Double> result = new TreeMap<>();

        try {
            List<String> lines = getListOfLines(FILE_NAME);
            for (String line : lines) {
                // Format: yyyy-MM-dd | projectName | HH:mm - HH:mm | X.XX hours
                String[] parts = line.split("\\|");
                if (parts.length < 4) continue;

                if (projectName.equals(parts[1].trim())) {
                    double hours = Double.parseDouble(parts[3].trim().split(" ")[0]);

                    result.put(projectName, result.getOrDefault(projectName, 0.0) + hours);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading log: " + e.getMessage());
        }

        return result;
    }

    private List<String> getListOfLines(String uri) throws IOException {
        return Files.readAllLines(Paths.get(FILE_NAME));
    }
}
