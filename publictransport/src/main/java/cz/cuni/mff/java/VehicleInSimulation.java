package cz.cuni.mff.java;
import java.util.Arrays;

public class VehicleInSimulation {
    private int id;
    private VehicleTypes type;
    private String model;
    private int capacity;
    private String history;
    private int pricePerKm;
    private Place [] PlannedStops;
    private Place [] currentPassagers; // each represented as his final stop in array
    private int DistanceTraveledMeters;
    private Coordinates currentCoordinates;
    public VehicleInSimulation(Vehicle vehicle, Place [] plannedStops) {
        this.id = vehicle.getId();
        this.type = vehicle.getType();
        this.capacity = vehicle.getCapacity();
        this.model = vehicle.getModel();
        this.history = vehicle.getHistory();
        this.pricePerKm = vehicle.getPricePerKm();
        this.PlannedStops = plannedStops;
        this.currentPassagers = new Place[capacity];
        this.DistanceTraveledMeters = 0;
        this.currentCoordinates = plannedStops[0].getCoordinates();
    }
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
        return PlannedStops;
    }
    public Place[] getCurrentPassagers() {
        return currentPassagers;
    }
    public int getDistanceTraveledMeters() {
        return DistanceTraveledMeters;
    }
    public Coordinates getCurrentCoordinates() {
        return currentCoordinates;
    }
    public void updateDistanceTraveledMeters(int distance) {
        if (distance < 0) {
            throw new IllegalArgumentException("Distance cannot be negative");
        }
        this.DistanceTraveledMeters += distance;
    }
    public void addHistory(String history) {
        this.history += history;
    }
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
            if (currentPassagers[i] == null && newPassagers[i] != null && Arrays.asList(PlannedStops).contains(newPassagers[i])) {
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
            if (Arrays.asList(PlannedStops).contains(passager)) {
                count++;
            }
        }
        return count;
    }
    public void updateCurrentCoordinates(Place stop) {
        this.currentCoordinates = stop.getCoordinates();
    }
}
