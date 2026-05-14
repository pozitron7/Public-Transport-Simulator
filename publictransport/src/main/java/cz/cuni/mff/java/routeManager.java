package cz.cuni.mff.java;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;

public class RouteManager {
    private List<ScheduledTrip> sortedDailySchedule;
    private int numberOfTrips;
    private int lastIndex = -1; //last started strip, we start with -1 and then increment it
    public RouteManager(List<ScheduledTrip> dailySchedule) {
        this.sortedDailySchedule = new ArrayList<>(dailySchedule);
        this.numberOfTrips = sortedDailySchedule.size();
        sortedDailySchedule.sort((t1, t2) -> Integer.compare(t1.getStartTimeSeconds(), t2.getStartTimeSeconds()));
    }
    public List<ScheduledTrip> getTripsStartingAt(int currentSimulationTime) {
        List<ScheduledTrip> startingNow = new ArrayList<>();
        
        while (lastIndex+1 < sortedDailySchedule.size() && sortedDailySchedule.get(lastIndex + 1).getStartTimeSeconds() <= currentSimulationTime) {
            if (sortedDailySchedule.get(lastIndex+1).getStartTimeSeconds() == currentSimulationTime) {
                startingNow.add(sortedDailySchedule.get(lastIndex + 1));
            }
            lastIndex++;
        }
        return startingNow;
    }
    // getters
    public List<ScheduledTrip> getSortedDailySchedule() {
        return sortedDailySchedule;
    }
    public int getNumberOfTrips() {
        return numberOfTrips;
    }
    public Place [] getStops() {
        Set<Place> uniqueStops = new HashSet<>();
        for (ScheduledTrip trip : sortedDailySchedule) {
            for (Place stop : trip.getRoute().getStops()) {
                uniqueStops.add(stop);
            }
        }
        return uniqueStops.toArray(new Place[0]);
    }
    
    public Place[] getPlannedStopsForVehicle(Vehicle vehicle) {
        for (ScheduledTrip trip : sortedDailySchedule) {
            if (trip.getVehicle().equals(vehicle)) {
                return trip.getRoute().getStops();
            }
        }
        throw new IllegalArgumentException("Vehicle not found in schedule");
    }

}


