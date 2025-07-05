package record;

public record TimeEstimate(
        String projectName,
        double cd1EstimateHours,
        double cd2EstimateHours,
        double pfEstimateHours
) {
    @Override
    public String toString() {
        return String.format("%nProject name: %s%n" +
                        "CD1 time estimate: %.2f%n" +
                        "CD2 time estimate: %.2f%n" +
                        "PF time estimate: %.2f%n" +
                        "Total estimate hours: %.2f%n",
                projectName,
                cd1EstimateHours,
                cd2EstimateHours,
                pfEstimateHours,
                cd1EstimateHours + cd2EstimateHours + pfEstimateHours);
    }
}
