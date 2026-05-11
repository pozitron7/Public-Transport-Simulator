package cz.cuni.mff.java;

public class Route {
    private int id;
    private Stop[] stops;
    private int[] waitTimesSeconds;

    public Route(int id, Stop[] stops, int[] waitTimesSeconds) {
        this.id = id;
        this.stops = stops;
        this.waitTimesSeconds = waitTimesSeconds;
    }
    public int getId() {
        return id;
    }
    public Stop[] getStops() {
        return stops;
    }
    public int[] getWaitTimesSeconds() {
        return waitTimesSeconds;
    }
}