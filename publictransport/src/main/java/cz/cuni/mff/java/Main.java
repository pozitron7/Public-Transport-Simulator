package cz.cuni.mff.java;

public class Main {
    public static void main(String[] args) {
        ImportData dataLoader = new ImportData();
        dataLoader.importData(); // Loads all txt files

        RouteManager routeManager = new RouteManager(java.util.Arrays.asList(dataLoader.getSchedule()));
        PassagerGeneration passGen = dataLoader.getPassagerGeneration();
        DistanceManager[] distanceManagers = dataLoader.getDistanceManager();

        Simulator simulator = new Simulator(routeManager, passGen, distanceManagers);

        int startTime = 6 * 3600; // 06:00:00
        int endTime = 7 * 3600;   // 07:00:00
        
        System.out.println("Running simulation from " + startTime + " to " + endTime);
        simulator.runSimulation(startTime, endTime);
        System.out.println("Simulation finished. Passengers left behind: " + simulator.numberOfPassagersLeftBehind);
    }
}