package cz.cuni.mff.java;

public class PassagerGeneration {
    Stop[] stops;
    int [] averagePassengersAtStopPerminute;
    // expects order of stops to be the same as order of average passengers at stop per minute
    public PassagerGeneration(Stop[] stops, int[] averagePassengersAtStopPerminute) {
        this.stops = stops;
        this.averagePassengersAtStopPerminute = averagePassengersAtStopPerminute;

    }
    private int getIndexOfStop(Stop stop) {
        for (int i = 0; i < stops.length; i++) {
            if (stops[i].equals(stop)) {
                return i;
            }
        }
        throw new IllegalArgumentException("Stop not found in the list of stops.");
    }
    public int getAveragePassengersAtStopPerMinute(Stop stop) {
        int index = getIndexOfStop(stop);
        return averagePassengersAtStopPerminute[index];
    }
    // randomlygenerates passagers with final destination based on average number of pasagers generated there, it mimics stops popularity
    public Place [] generatePassagesWithFinalDestination(Place start, Place [] validDestinations, int numberOfPassagersToGenerate) {
        if ( numberOfPassagersToGenerate > 0) {
            int [] likelihoods = new int[validDestinations.length];
            double totalWeight = 0;
            for (int i = 0; i < validDestinations.length; i++) {
                likelihoods[i] = averagePassengersAtStopPerminute[i];
                totalWeight += likelihoods[i];
            }
            // we sum likelihoods and generate number between 0 and total weight,
            // then final stops are distributed based on density of ikelihoods
            Place [] generatedPassagers = new Place[numberOfPassagersToGenerate];
            java.util.Random random = new java.util.Random();

            for (int p = 0; p < numberOfPassagersToGenerate; p++) {
                double r = random.nextDouble() * totalWeight; 
                double runningSum = 0.0;
                for (int i = 0; i < validDestinations.length; i++) {
                    runningSum += likelihoods[i];
                    if (r < runningSum) {
                        generatedPassagers[p] = validDestinations[i];
                        break;
                    }
                }
            }
            return generatedPassagers;
        }
        else if (numberOfPassagersToGenerate == 0) {
            return new Place[0];
        }
        else {
            throw new IllegalArgumentException("Number of passagers to generate cannot be negative.");
        }

    }
}
