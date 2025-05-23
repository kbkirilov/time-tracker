package record;
import java.time.LocalDate;
import java.time.LocalTime;

public record TimeEntry(String projectName, LocalDate date, LocalTime start, LocalTime end) {
    @Override
    public String toString() {
        return String.format("Project name: %s, Entry date: %s, Start time: %s, End time: %s",
                projectName(),
                date(),
                start(),
                end());
    }
}
