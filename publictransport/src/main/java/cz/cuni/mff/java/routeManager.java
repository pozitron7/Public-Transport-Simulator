package cz.cuni.mff.java;
import java.util.List;
import java.util.ArrayList;

public class RouteManager {
    private List<ScheduledTrip> sortedDailySchedule;
    private int lastIndex = 0;
    public RouteManager(List<ScheduledTrip> dailySchedule) {
        this.sortedDailySchedule = new ArrayList<>(dailySchedule);
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
}


