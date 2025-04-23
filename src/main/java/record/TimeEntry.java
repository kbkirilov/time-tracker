package record;
import java.time.LocalDate;
import java.time.LocalTime;

public record TimeEntry(String projectName, LocalDate date, LocalTime start, LocalTime end) {}
