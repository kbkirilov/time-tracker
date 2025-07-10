package record;
import java.time.LocalDate;
import java.time.LocalTime;

public record TimeEntry(String projectName, String projectStage, LocalDate date, LocalTime start, LocalTime end, int timeVariance) {
    @Override
    public String toString() {
        return String.format("Project name: %s, Project stage: %s, Entry date: %s, Start time: %s, End time: %s, Time variance: %d",
                projectName(),
                projectStage(),
                date(),
                start(),
                end(),
                timeVariance());
    }
}
