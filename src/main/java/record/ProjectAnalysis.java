package record;

public record ProjectAnalysis(
        String projectName,
        double actualCD1Hours,
        double actualCD2Hours,
        double actualPFHours,
        double estimatedCD1Hours,
        double estimatedCD2Hours,
        double estimatedPFHours
) {
    /**
     * Gets the actual total hours for all project stage combined
     * @return The sum of all project stages actual hours
     */
    public double getTotalActualHours() {
        return actualCD1Hours + actualCD2Hours + actualPFHours;
    }

    /**
     * Gets the estimate total hours for all project stage combined
     * @return The total estimate hours for all project stages combined
     */
    public double getTotalEstimatedHours() {
        return estimatedCD1Hours + estimatedCD2Hours + estimatedPFHours;
    }

    /**
     * Gets how many more hours do I have left for CD1 before I reach the limit. If the return value is positive I
     * have x more hours left. If the return value is negative I have crossed the hour budget
     * @return hours left till
     */
    public double getCD1Variance() {
        return estimatedCD1Hours - actualCD1Hours;
    }

    public double getCD2Variance() {
        return estimatedCD2Hours - actualCD2Hours;
    }

    public double getPFVariance() {
        return estimatedPFHours - actualPFHours;
    }

    public double getTotalVariance() {
        return getTotalEstimatedHours() - getTotalActualHours();
    }

    public double getCD1PercentageVariance() {
        return estimatedCD1Hours > 0 ? (getCD1Variance() / estimatedCD1Hours) * 100 : 0;
    }

    public double getCD2PercentageVariance() {
        return estimatedCD2Hours > 0 ? (getCD2Variance() / estimatedCD2Hours) * 100 : 0;
    }

    public double getPFPercentageVariance() {
        return estimatedPFHours > 0 ? (getPFVariance() / estimatedPFHours) * 100 : 0;
    }
}
