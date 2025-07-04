package record;

public record TimeEstimate(
        String projectName,
        double cd1EstimateHours,
        double cd2EstimateHours,
        double pfEstimateHours
) {}
