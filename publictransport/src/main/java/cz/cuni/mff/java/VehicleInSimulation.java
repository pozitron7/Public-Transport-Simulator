package cz.cuni.mff.java;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

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
    private int numberOfTransportedPassagers; // every passenger that entered and leaved vehicle is counted as transported

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
        this.state = VehicleState.WAITING_FOR_START;
        this.departureTimeSeconds = departureTimeSeconds;
        this.currentPlace = null;
        this.numberOfTransportedPassagers = 0;
        this.timeOfNextStateChange = departureTimeSeconds;
    }
    public enum VehicleState { 
        DRIVING, 
        WAITING_AT_STOP, 
        FINISHED,
        WAITING_FOR_START
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
    public int getNumberOfTransportedPassagers() {
        return numberOfTransportedPassagers;
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
    public void unloadPassagersAtStop(Place stop){ 
        for (int i = 0; i < currentPassagers.length; i++) {
            if (currentPassagers[i] != null && currentPassagers[i].equals(stop)) {
                numberOfTransportedPassagers++;
                currentPassagers[i] = null;
            }
        }
    }
    public Place[] loadPassagers(Place[] newPassagers) { // try to board as many passagers as possible and return rest of them
        List<Place> passagersToBoard = new ArrayList<>();
        for (Place passager : newPassagers) {
            if (Arrays.asList(plannedStops).contains(passager)) {
                passagersToBoard.add(passager);
            } 
        }
        // try board passagesr based on bus capacity
        int indexOfNexgPassagerToBoard = 0;
        List<Place> passegersBoarded = new ArrayList<>();
        for (int i = 0; i < currentPassagers.length; i++){
            if (indexOfNexgPassagerToBoard >= passagersToBoard.size()){ break;}
            if (currentPassagers[i] == null){
                Place passager = passagersToBoard.get(indexOfNexgPassagerToBoard);
                currentPassagers[i] = passager;
                indexOfNexgPassagerToBoard = indexOfNexgPassagerToBoard + 1;
                passegersBoarded.add(passager);

            }
        }
        List<Place> unboardedPassagersList = new ArrayList<>(Arrays.asList(newPassagers));
        for (Place boardedPassager : passegersBoarded) {
            unboardedPassagersList.remove(boardedPassager);// removes just first occurance
        }

        return unboardedPassagersList.toArray(new Place[0]);
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
