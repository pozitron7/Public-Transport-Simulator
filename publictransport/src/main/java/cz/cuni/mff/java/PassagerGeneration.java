package cz.cuni.mff.java;

public class PassagerGeneration {
    Place[] stops;
    double [] averagePassengersAtStopPerminute;
    // expects order of stops to be the same as order of average passengers at stop per minute
    public PassagerGeneration(Place[] stops, double[] averagePassengersAtStopPerminute) {
        this.stops = stops;
        this.averagePassengersAtStopPerminute = averagePassengersAtStopPerminute;

    }
    private int getIndexOfStop(Place stop) {
        for (int i = 0; i < stops.length; i++) {
            if (stops[i].equals(stop)) {
                return i;
            }
        }
        throw new IllegalArgumentException("Stop not found in the list of stops.");
    }
    public double getAveragePassengersAtStopPerMinute(Place stop) {
        int index = getIndexOfStop(stop);
        return averagePassengersAtStopPerminute[index];
    }
    public int getNumberOfPassagersThatAppearAtStopPoason(int timeIntervalSeconds, Place stop){
        double averagePerMinute = getAveragePassengersAtStopPerMinute(stop);
        double lambda = averagePerMinute * (timeIntervalSeconds / 60.0);

        if (lambda <= 0.0) return 0;
        if (lambda > 500.0) {
            // For large lambda, the Poisson distributionis almopst same as a normal distribution
            double mean = lambda;
            double stddev = Math.sqrt(lambda);
            return (int) Math.round(mean + stddev * new java.util.Random().nextGaussian());
        }
        // Knuth's Algorithm generates poisson distribution but works only for small lambda here it is ok
        double L = Math.exp(-lambda);
        int k = 0;
        double p = 1.0;
        do {
            k++;
            p *= Math.random();
        } while (p > L);
        return k - 1;
    }
    // randomlygenerates passagers with final destination based on average number of pasagers generated there, it mimics stops popularity
    public Place [] generatePassagesWithFinalDestination(Place start, Place [] validDestinations, int numberOfPassagersToGenerate) {
        if ( numberOfPassagersToGenerate > 0) {
            double [] likelihoods = new double[validDestinations.length];
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
