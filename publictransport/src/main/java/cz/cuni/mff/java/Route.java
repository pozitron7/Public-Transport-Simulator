package cz.cuni.mff.java;

public class Route {
    private int id;
    private Place[] stops;
    private int[] waitTimesSeconds;

    public Route(int id, Place[] stops, int[] waitTimesSeconds) {
        this.id = id;
        this.stops = stops;
        this.waitTimesSeconds = waitTimesSeconds;
    }
    public int getId() {
        return id;
    }
    public Place[] getStops() {
        return stops;
    }
    public int[] getWaitTimesSeconds() {
        return waitTimesSeconds;
    }
}