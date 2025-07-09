package service;

import record.TimeEntry;
import record.TimeEstimate;

import java.time.Duration;

import static utils.Calculations.calculateRoundedHours;


public class LogService {
    private final DatabaseService db;
    private final DisplayService displayService;

    public LogService(DatabaseService db, DisplayService displayService) {
        this.db = db;
        this.displayService = displayService;
    }

    public void logInsertTimeEntry(TimeEntry entry) {
        double roundedHours = calculateRoundedHours(entry);

        System.out.println("Logging time for project: " + entry.projectName());
        displayService.displayTimeEntryDetails(entry);

        db.createTimeEntry(entry, roundedHours);
    }

    public void logInsertTimeEstimate(TimeEstimate entry) {
        double totalHours = entry.cd1EstimateHours() + entry.cd2EstimateHours() + entry.pfEstimateHours();

        displayService.displayTimeEstimateDetails(entry);

        db.createTimeEstimateEntry(entry, totalHours);
    }

    public void logDeleteTimeEntry(int id) {
        db.deleteTimeEntryById(id);  // без проверка
    }

    public void logDeleteTimeEstimateEntry(int id) {
        db.deleteTimeEstimateEntryById(id);
    }

    public void logUpdateTimeEntry(int id, TimeEntry entry) {
        double roundedHours = calculateRoundedHours(entry);
        System.out.println("Updating project with name: " + entry.projectName());
        displayService.displayTimeEntryDetails(entry);

        db.updateTimeEntryById(id, entry, roundedHours);

    }

    public void logUpdatedTimeEstimate(int id, TimeEstimate entry) {
        double totalHours = entry.cd1EstimateHours() + entry.cd2EstimateHours() + entry.pfEstimateHours();
        System.out.println("Updating project with name: " + entry.projectName());

        db.updateTimeEstimateById(id, entry, totalHours);
    }

}

