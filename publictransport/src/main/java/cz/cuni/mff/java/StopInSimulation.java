package cz.cuni.mff.java;
import java.util.List;

public class StopInSimulation extends Stop {
    private List<Place> waitingPassengers; //each passager is represented as its final destination
    public StopInSimulation(String name, int id, Coordinates coordinates, VehicleTypes type, List<Place> waitingPassengers) {
        super(name, id, coordinates, type);
        this.waitingPassengers = waitingPassengers;
    }
    public List<Place> getWaitingPassengers() {
        return waitingPassengers;
    }
    public void addWaitingPassenger(Place [] passengers) {
        waitingPassengers.addAll(List.of(passengers));
    }
    public void setWaitingPassengers(Place[] passengers) {
        waitingPassengers.clear();
        waitingPassengers.addAll(List.of(passengers));
    }
    public void removeWaitingPassenger(Place[] passengers) {
        waitingPassengers.removeAll(List.of(passengers));
    }
}
