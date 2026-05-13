package cz.cuni.mff.java;
import java.util.Arrays;

// Represents a vehicle driving particular route in simulation.
public class VehicleInSimulation {
    private int id;
    private VehicleTypes type;
    private String model;
    private int capacity;
    private String history;
    private int pricePerKm;
    private Place [] plannedStops;
    private Route route;
    private Place [] currentPassagers; // each represented as his final stop in array
    private int distanceTraveledMeters;
    private Place currentPlace;
    private int timeOfNextStateChange;
    private VehicleState state;
    private int departureTimeSeconds; 
    public VehicleInSimulation(Vehicle vehicle, Route route, int departureTimeSeconds) {
        this.id = vehicle.getId();
        this.type = vehicle.getType();
        this.capacity = vehicle.getCapacity();
        this.model = vehicle.getModel();
        this.history = vehicle.getHistory();
        this.pricePerKm = vehicle.getPricePerKm();
        this.route = route;
        this.plannedStops = route.getStops();
        this.currentPassagers = new Place[capacity];
        this.distanceTraveledMeters = 0;
        this.state = VehicleState.WAITING_AT_STOP;
        this.departureTimeSeconds = departureTimeSeconds;
        this.currentPlace = plannedStops[0];
    }
    public enum VehicleState { 
        DRIVING, 
        WAITING_AT_STOP, 
        FINISHED 
    }

    // defining getters and setters for all fields
    public int getId() {
        return id;
    }
    public VehicleTypes getType() {
        return type;
    }
    public int getCapacity() {
        return capacity;
    }
    public String getModel() {
        return model;
    }
    public String getHistory() {
        return history;
    }
    public int getPricePerKm() {
        return pricePerKm;
    }
    public Place[] getPlannedStops() {
        return plannedStops;
    }
    public Place[] getCurrentPassagers() {
        return currentPassagers;
    }
    public int getDistanceTraveledMeters() {
        return distanceTraveledMeters;
    }
    public Place getCurrentPlace() {
        return currentPlace;
    }
    public void updateCurrentPlace(Place stop) {
        this.currentPlace = stop;
    }
    public VehicleState getState() {
        return state;
    }
    public void setState(VehicleState state) {
        this.state = state;
    }
    public Route getRoute() {
        return route;
    }
    public int getDepartureTimeSeconds() {
        return departureTimeSeconds;
    }
    public int getTimeOfNextStateChange() {
        return timeOfNextStateChange;
    }
    public void setTimeOfNextStateChange(int timeOfNextStateChange) {
        this.timeOfNextStateChange = timeOfNextStateChange;
    }
    public void updateDistanceTraveledMeters(int distance) {
        if (distance < 0) {
            throw new IllegalArgumentException("Distance cannot be negative");
        }
        this.distanceTraveledMeters += distance;
    }
    public void addHistory(String history) {
        this.history += history;
    }


    // logic for passager entering and exiting the vehicle
    public void passagersExiting(Place stop){ 
        for (int i = 0; i < currentPassagers.length; i++) {
            if (currentPassagers[i] != null && currentPassagers[i].equals(stop)) {
                currentPassagers[i] = null;
            }
        }
    }
    public Place[] passagersEntering(Place[] newPassagers) { // try to board as many passagers as possible and return rest of them
        int boardedCount = 0;
        for (int i = 0; i < currentPassagers.length && boardedCount < newPassagers.length; i++) {
            if (currentPassagers[i] == null && newPassagers[i] != null && Arrays.asList(plannedStops).contains(newPassagers[i])) {
                currentPassagers[i] = newPassagers[boardedCount];
                boardedCount++; 
            }
        }
        if (boardedCount == newPassagers.length) {
            return new Place[0];
        }
        return Arrays.copyOfRange(newPassagers, boardedCount, newPassagers.length);
    }
    public int howManyPassagerWantToRideWithMe(Place [] Passagers){
        int count = 0;
        for (Place passager : Passagers) {
            if (Arrays.asList(plannedStops).contains(passager)) {
                count++;
            }
        }
        return count;
    }


    
}
