package cz.cuni.mff.java;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

public class Simulator {
    private RouteManager routeManager;
    private VehicleInSimulation[] sortedVehiclesInSimulation; // sorted by departure time, each vehicle ropresents one trip in schedule
    private StopInSimulation[] stopsInSimulation;
    private PassagerGeneration passagerGeneration;
    private List<VehicleInSimulation> vehiclesCurrentlyInSimulation;
    private Map<VehicleTypes, DistanceManager> distanceManagers;
    private Map<Integer, java.util.Set<Place>> validDestinationsForStopId; // is used for passager generation
    public Simulator(RouteManager routeManager, PassagerGeneration passagerGeneration, DistanceManager [] distanceManagers) {
        this.routeManager = routeManager;
        this.passagerGeneration = passagerGeneration;
        this.sortedVehiclesInSimulation = createVehicleInSimulation(routeManager);
        this.stopsInSimulation = createStopsInSimulation(routeManager);
        this.vehiclesCurrentlyInSimulation = new ArrayList<>();
        initializeDistanceManagers(distanceManagers);
        initializeValidDestinationsForStops();
    }
    private void initializeDistanceManagers(DistanceManager [] distanceManagers) {
        for (DistanceManager dm : distanceManagers) {
            this.distanceManagers.put(dm.getType(), dm);
        }
    }
    private void initializeValidDestinationsForStops() {
        this.validDestinationsForStopId = new java.util.HashMap<>();
        Set<Integer> processedRoutes = new HashSet<>();
        for (ScheduledTrip trip : routeManager.getSortedDailySchedule()) {
            Route route = trip.getRoute();
            if (processedRoutes.contains(route.getId())) {
                continue;
            }
            processedRoutes.add(route.getId());
            Place[] stops = route.getStops();
            for (int i = 0; i < stops.length; i++) {
                Place currentStop = stops[i];
            
                validDestinationsForStopId.putIfAbsent(currentStop.getId(), new java.util.HashSet<>());
                java.util.Set<Place> validDests = validDestinationsForStopId.get(currentStop.getId());
                // we add all folowing stops in route
                for (int j = i + 1; j < stops.length; j++) {
                    validDests.add(stops[j]);
                }
            }
        }
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
    // function expects that event changed and we calculate what is next event for this vehicle and update its state and time of next state change
   private void updateVehicleState(VehicleInSimulation vehicle, int currentTimeSeconds) {
        switch (vehicle.getState()) {
            case VehicleInSimulation.VehicleState.WAITING_AT_STOP: {
                Place currentStop = vehicle.getCurrentPlace();
                int stopindex = 0;
                for (int i = 0; i < vehicle.getPlannedStops().length; i++) {
                    if (vehicle.getPlannedStops()[i].equals(currentStop)) {
                        stopindex = i;
                        break;
                    }
                }
                // vehicle finished journey
                if (stopindex == vehicle.getPlannedStops().length - 1) {
                    vehicle.setTimeOfNextStateChange(Integer.MAX_VALUE);
                    vehicle.updateCurrentPlace(vehicle.getPlannedStops()[stopindex]);
                    vehicle.setState(VehicleInSimulation.VehicleState.FINISHED);
                    // we remove vehicle from simulation
                    vehiclesCurrentlyInSimulation.remove(vehicle);
                }
                // we set vehicle to driving
                else {
                    vehicle.setState(VehicleInSimulation.VehicleState.DRIVING);
                    int timeToNextStop = distanceManagers.get(vehicle.getType()).getDistanceSeconds(vehicle.getPlannedStops()[stopindex], vehicle.getPlannedStops()[stopindex+1]);
                    vehicle.setTimeOfNextStateChange(currentTimeSeconds + timeToNextStop);
                    // current place stays same until we update it when vehicle arrives at next stop
                }
                
                break;
            }
        
            case VehicleInSimulation.VehicleState.DRIVING: {
                Place lastPlace = vehicle.getCurrentPlace();
                int stopindex = 0;
                for (int i = 0; i < vehicle.getPlannedStops().length; i++) {
                    if (vehicle.getPlannedStops()[i].equals(lastPlace)) {
                        stopindex = i;
                        break;
                    }
                }
                vehicle.updateCurrentPlace(vehicle.getPlannedStops()[stopindex+1]);
                vehicle.setState(VehicleInSimulation.VehicleState.WAITING_AT_STOP);
                int waitTimeSeconds = vehicle.getRoute().getWaitTimesSeconds()[stopindex+1];
                vehicle.setTimeOfNextStateChange(currentTimeSeconds + waitTimeSeconds);
                int distanceTraveled = distanceManagers.get(vehicle.getType()).getDistanceMeters(vehicle.getPlannedStops()[stopindex], vehicle.getPlannedStops()[stopindex+1]);
                vehicle.updateDistanceTraveledMeters(distanceTraveled);
                break;
            }
            default:
                throw new IllegalStateException("Unexpected value: " + vehicle.getState());
                
            
        }
    }
    // ge generate random number of passagers that arived in timeWindow seconds at each stop
    private void generatePassagersAtStops(int timeWindow) {
        for (StopInSimulation stop : stopsInSimulation) {
            int numberOfPassagersToGenerate = passagerGeneration.getNumberOfPassagersThatAppearAtStopPoason(timeWindow, stop);
            Place [] validDestinations = validDestinationsForStopId.getOrDefault(stop.getId(), new java.util.HashSet<>()).toArray(new Place[0]);
            Place [] passagers = passagerGeneration.generatePassagesWithFinalDestination(stop, validDestinations, numberOfPassagersToGenerate);
            stop.addWaitingPassenger(passagers);
        }
    }

    private void initializeSimulation(int simulationStartInSeconds) {
        vehiclesCurrentlyInSimulation = new ArrayList<>();
        for (VehicleInSimulation vehicle : sortedVehiclesInSimulation) {
            if (vehicle.getDepartureTimeSeconds() == simulationStartInSeconds) {
                vehiclesCurrentlyInSimulation.add(vehicle);
            }
        }
    }
    private void updateSimulationState(int currentTimeSeconds) {
        for (VehicleInSimulation vehicle : vehiclesCurrentlyInSimulation) {
            if (vehicle.getTimeOfNextStateChange() == currentTimeSeconds) {
                updateVehicleState(vehicle, currentTimeSeconds);
            }
        }
    }
    public void runSimulation(ImportData data, PassagerGeneration passagerGeneration, int simulationStartInSeconds, int simulationEndSeconds) {
        RouteManager routeManager = new RouteManager(List.of(data.schedule));
        
            }
        
}
// in each iteration we find all vehicles that should start at time and add them to simulation
// in each iteration we check what vehicles arrived at some station so we need dict of expected arrivals
// update states of some vehicles

//small helper functions