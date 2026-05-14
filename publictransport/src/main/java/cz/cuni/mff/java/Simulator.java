package cz.cuni.mff.java;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

public class Simulator {
    private RouteManager routeManager;
    private VehicleInSimulation[] sortedVehiclesInSimulation; // sorted by departure time, each vehicle ropresents one trip in schedule
    private Map<Integer, StopInSimulation> getStopsInSimulations;
    private PassagerGeneration passagerGeneration;
    private List<VehicleInSimulation> vehiclesCurrentlyInSimulation;
    private Map<VehicleTypes, DistanceManager> distanceManagers;
    private Map<Integer, java.util.Set<Place>> validDestinationsForStopId; // is used for passager generation
    int numberOfPassagersLeftBehind = 0; // that is passager that wanted to board but could not becouse vehicle was full
    int numberOfPassengersGenerated = 0;
    int indexOfVehicleToAddNext = 0;
    public Simulator(RouteManager routeManager, PassagerGeneration passagerGeneration, DistanceManager [] distanceManagers) {
        this.routeManager = routeManager;
        this.passagerGeneration = passagerGeneration;
        this.sortedVehiclesInSimulation = createVehicleInSimulation(routeManager);
        createStopsInSimulation(routeManager);
        this.vehiclesCurrentlyInSimulation = new ArrayList<>();
        initializeDistanceManagers(distanceManagers);
        initializeValidDestinationsForStops();
    }
    private void initializeDistanceManagers(DistanceManager [] distanceManagers) {
        this.distanceManagers = new java.util.HashMap<>();
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
    private void createStopsInSimulation(RouteManager routeManager) {
        this.getStopsInSimulations = new java.util.HashMap<>();
        Place [] stops = routeManager.getStops();
        
        for (Place stop : stops) {
            StopInSimulation simStop = new StopInSimulation(stop.getName(), stop.getId(), stop.getCoordinates(), stop.getType(), new ArrayList<>());
            this.getStopsInSimulations.put(stop.getId(), simStop);
        }
    }
    // function expects that event changed and we calculate what is next event for this vehicle and update its state and time of next state change
   private void updateVehicleState(VehicleInSimulation vehicle, int currentTimeSeconds) {
        Place lastPlace = vehicle.getCurrentPlace();
                int stopindex = 0;
                for (int i = 0; i < vehicle.getPlannedStops().length; i++) {
                    if (vehicle.getPlannedStops()[i].equals(lastPlace)) {
                        stopindex = i;
                        break;
                    }
                }
        switch (vehicle.getState()) {
            case VehicleInSimulation.VehicleState.WAITING_AT_STOP: {
                
                // vehicle finished journey, we update its state and remove it from simulation
                if (stopindex == vehicle.getPlannedStops().length - 1) {
                    vehicle.setTimeOfNextStateChange(Integer.MAX_VALUE);
                    vehicle.updateCurrentPlace(vehicle.getPlannedStops()[stopindex]);
                    vehicle.setState(VehicleInSimulation.VehicleState.FINISHED);
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
                StopInSimulation stop = getStopsInSimulations.get(vehicle.getPlannedStops()[stopindex+1].getId());
                 // we update vehicle current place to next stop
                vehicle.updateCurrentPlace(stop);
                vehicle.setState(VehicleInSimulation.VehicleState.WAITING_AT_STOP);
                int waitTimeSeconds = vehicle.getRoute().getWaitTimesSeconds()[stopindex+1];
                vehicle.setTimeOfNextStateChange(currentTimeSeconds + waitTimeSeconds);
                int distanceTraveled = distanceManagers.get(vehicle.getType()).getDistanceMeters(vehicle.getPlannedStops()[stopindex], vehicle.getPlannedStops()[stopindex+1]);
                vehicle.updateDistanceTraveledMeters(distanceTraveled);


                System.out.println("Time " + currentTimeSeconds + ": Vehicle " + vehicle.getId() + " arrived at " + stop.getName());


                break;
            }
            case VehicleInSimulation.VehicleState.WAITING_FOR_START: {
                vehicle.setState(VehicleInSimulation.VehicleState.WAITING_AT_STOP);
                int waitTimeSeconds = vehicle.getRoute().getWaitTimesSeconds()[0];
                vehicle.setTimeOfNextStateChange(currentTimeSeconds + waitTimeSeconds);
                break;
            }
            default:
                throw new IllegalStateException("Unexpected value: " + vehicle.getState());
                
            
        }
    }
    // we generate random number of passagers that arived in timeWindow seconds at each stop
    private void generatePassagersAtStops(int timeWindow) {
        for (StopInSimulation stop : getStopsInSimulations.values()) {
            int numberOfPassagersToGenerate = passagerGeneration.getNumberOfPassagersThatAppearAtStopPoason(timeWindow, stop);
            Place [] validDestinations = validDestinationsForStopId.getOrDefault(stop.getId(), new java.util.HashSet<>()).toArray(new Place[0]);
            Place [] passagers = passagerGeneration.generatePassagesWithFinalDestination(stop, validDestinations, numberOfPassagersToGenerate);
            stop.addWaitingPassenger(passagers);
            numberOfPassengersGenerated += numberOfPassagersToGenerate;
        }
    }

    private void loadAndUnloadPassagersAtStop(VehicleInSimulation vehicle, StopInSimulation stop) {
        // unload passager
        vehicle.unloadPassagersAtStop(stop);
        // load passagers
        List<Place> waitingPassagers = stop.getWaitingPassengers();
        Place [] waitingPassagersArray = waitingPassagers.toArray(new Place[0]);
        // this already loaded passagers
        Place [] remainingPassagers = vehicle.loadPassagers(waitingPassagersArray);
        // we update stop waiting passagers to remaining passagers that could not board
        int numberOfPassagersLeftBehind = vehicle.howManyPassagerWantToRideWithMe(remainingPassagers);
        this.numberOfPassagersLeftBehind += numberOfPassagersLeftBehind;
        stop.setWaitingPassengers(remainingPassagers);

    }
    private void addVehiclesToSimulation(int simulationStartInSeconds) {
        for (int i = indexOfVehicleToAddNext; i < routeManager.getSortedDailySchedule().size(); i++) {
            VehicleInSimulation vehicle = sortedVehiclesInSimulation[i];
            if (vehicle.getDepartureTimeSeconds() <= simulationStartInSeconds) {
                vehiclesCurrentlyInSimulation.add(vehicle);
                StopInSimulation stop = getStopsInSimulations.get(vehicle.getPlannedStops()[0].getId());
                vehicle.updateCurrentPlace(stop);
                indexOfVehicleToAddNext++;
            }
            else {
                break;
            }
            
        }
    }
    private void updateSimulationState(int currentTimeSeconds) {
        addVehiclesToSimulation(currentTimeSeconds);
        // we use iterator so we can remove vehicle object without crashing
        java.util.Iterator<VehicleInSimulation> iterator = vehiclesCurrentlyInSimulation.iterator();
        while (iterator.hasNext()) {
            VehicleInSimulation vehicle = iterator.next();
            while (vehicle.getTimeOfNextStateChange() <= currentTimeSeconds && vehicle.getState() != VehicleInSimulation.VehicleState.FINISHED) {
                updateVehicleState(vehicle, currentTimeSeconds);
                
                if (vehicle.getState() == VehicleInSimulation.VehicleState.WAITING_AT_STOP) {
                    if (vehicle.getTimeOfNextStateChange() > currentTimeSeconds) {
                        Place stop = vehicle.getCurrentPlace();
                        StopInSimulation simStop = (StopInSimulation) stop;
                        loadAndUnloadPassagersAtStop(vehicle, simStop);
                    }
                }
            }
            
            if (vehicle.getState() == VehicleInSimulation.VehicleState.FINISHED) {
                iterator.remove();
            }
        }
        if (currentTimeSeconds % 60 == 0) { 
            generatePassagersAtStops(60);
        }
    }
    public void runSimulation(int simulationStartInSeconds, int simulationEndInSeconds) {
        for (int currentTime = simulationStartInSeconds; currentTime <= simulationEndInSeconds; currentTime++) {
            updateSimulationState(currentTime);
        }
    }

// in each iteration we find all vehicles that should start at time and add them to simulation
// in each iteration we check what vehicles arrived at some station so we need dict of expected arrivals
// update states of some vehicles

//in the end remind me to ask how to clear simulation after its done
//
    
}