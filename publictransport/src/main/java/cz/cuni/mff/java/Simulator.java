package cz.cuni.mff.java;
import java.util.List;
import java.util.ArrayList;
public class Simulator {
    private RouteManager routeManager;
    private VehicleInSimulation[] sortedVehiclesInSimulation; // sorted by departure time, each vehicle ropresents one trip in schedule
    private StopInSimulation[] stopsInSimulation;
    private PassagerGeneration passagerGeneration;
    public Simulator(RouteManager routeManager, PassagerGeneration passagerGeneration) {
       this.routeManager = routeManager;
       this.passagerGeneration = passagerGeneration;
       this.sortedVehiclesInSimulation = createVehicleInSimulation(routeManager);
         this.stopsInSimulation = createStopsInSimulation(routeManager);
    }
    private VehicleInSimulation [] createVehicleInSimulation(RouteManager routeManager) {
        VehicleInSimulation [] vehiclesInSimulation = new VehicleInSimulation[routeManager.getNumberOfTrips()];
        for (int i = 0; i < routeManager.getSortedDailySchedule().size(); i++) {
            ScheduledTrip trip = routeManager.getSortedDailySchedule().get(i);
            Vehicle vehicle = trip.getVehicle();
            vehiclesInSimulation[i] = new VehicleInSimulation(vehicle, trip.getRoute(), trip.getStartTimeSeconds());
        }
        return vehiclesInSimulation;
    }
    private StopInSimulation [] createStopsInSimulation(RouteManager routeManager) {
        Place [] stops = routeManager.getStops();
        StopInSimulation [] stopsInSimulation = new StopInSimulation[stops.length];
        for (int i = 0; i < stops.length; i++) {
            Place stop = stops[i];
            stopsInSimulation[i] = new StopInSimulation(stop.getName(), stop.getId(), stop.getCoordinates(), stop.getType(), new ArrayList<>());
        }
        return stopsInSimulation;
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
