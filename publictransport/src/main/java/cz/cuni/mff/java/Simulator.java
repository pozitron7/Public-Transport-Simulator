package cz.cuni.mff.java;
import java.util.List;
public class Simulator {
    private RouteManager routeManager;
    private List<VehicleInSimulation> vehiclesInSimulation;
    private List<StopInSimulation> stopsInSimulation;
    private PassagerGeneration passagerGeneration;
    public Simulator(RouteManager routeManager, List<Vehicle> vehicles, List<Place> stops, PassagerGeneration passagerGeneration) {
       this.routeManager = routeManager;
       this.passagerGeneration = passagerGeneration;
       for (Vehicle vehicle : vehicles) {
           Place [] plannedStops = routeManager.getPlannedStopsForVehicle(vehicle);
           vehiclesInSimulation.add(new VehicleInSimulation(vehicle, plannedStops));
       }
    }
    private void initializeSimulation(ImportData data) {
        // here we can initialize any data structures we need for the simulation, for example we can create a map of place id to place object for quick access
    }
    private void updateSimulationState(int currentTimeSeconds) {
        // here we can update the state of the simulation, for example we can check if any new trips should start at this time and add them to the simulation
    }
    public void runSimulation(ImportData data, PassagerGeneration passagerGeneration, int simulationStartInSeconds, int simulationEndSeconds) {
        RouteManager routeManager = new RouteManager(List.of(data.schedule));
        
            }
        
}
