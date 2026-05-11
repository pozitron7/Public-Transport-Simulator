package cz.cuni.mff.java;

public class ScheduledTrip {
    private Vehicle vehicle; 
    private Route route;     
    private int startTimeSeconds; 

    public ScheduledTrip(Vehicle vehicle, Route route, int startTimeSeconds) {
        this.vehicle = vehicle;
        this.route = route;
        this.startTimeSeconds = startTimeSeconds;
    }
    public Vehicle getVehicle() {
        return vehicle;
    }
    public Route getRoute() {
        return route;
    }
    public int getStartTimeSeconds() {
        return startTimeSeconds;
    }
}