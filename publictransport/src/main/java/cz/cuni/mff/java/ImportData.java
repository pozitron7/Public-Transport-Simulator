package cz.cuni.mff.java;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
public class ImportData {
    Vehicle[] vehicles;
    Place[] places;
    DistanceManager [] distanceManager;
    Route[] routes;
    ScheduledTrip [] schedule;
    Map<Integer, Place> placeById;
    PassagerGeneration passagerGeneration;
    private List<String> readDataFromFile(String filePath) {
        try {
            List<String> rawLines = Files.readAllLines(Path.of(filePath));
            List<String> cleanLines = new java.util.ArrayList<>();

            for (String line : rawLines) {
                String trimmed = line.trim();
                if (!trimmed.isEmpty() && !trimmed.startsWith("#")) {
                    cleanLines.add(trimmed);
                }
            }
            return cleanLines;
        } catch (java.io.IOException e) {
            e.printStackTrace();
            return List.of();
        }
    }
    private int parseTimeToSeconds(String time) {
        String[] parts = time.trim().split(":");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid time format: " + time);
        }
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        int seconds = Integer.parseInt(parts[2]);
        return hours * 3600 + minutes * 60 + seconds;
    }
    private Place [] findPlacesByIds(int[] ids) {
        Place[] result = new Place[ids.length];
        if (places == null) {
            throw new IllegalStateException("Places data must be imported before finding places by ids.");
        }
        if (placeById == null) {
            placeById = new java.util.HashMap<>();
            for (Place p : places) {
                placeById.put(p.getId(), p);
            }
        }
        for (int i = 0; i < ids.length; i++) {
            Place place = placeById.get(ids[i]);
            if (place == null) {
                throw new IllegalArgumentException("Place ID " + ids[i] + " not found.");
            }
            result[i] = place;
        }
        return result;
    }
    public Vehicle [] importVehicleData(String filePath) {
        // froamt of vehicle file # ID;TYPE;capacity;pricePerKm;model;history
        List<String> lines = readDataFromFile(filePath);
        vehicles = new Vehicle[lines.size()];
        for (int i = 0; i < lines.size(); i++) {
            try{
                String[] parts = lines.get(i).split(";");
                for (int j = 0; j < parts.length; j++) {
                    parts[j] = parts[j].trim();
                }
                int id = Integer.parseInt(parts[0]);
                VehicleTypes type = VehicleTypes.valueOf(parts[1]);
                int capacity = Integer.parseInt(parts[2]);
                int pricePerKm = Integer.parseInt(parts[3]);
                String model;
                if (parts.length <= 4 || parts[4].equals("-")) {
                    model = "Unknown";
                }
                else{model = parts[4];}
                String history;
                if (parts.length <= 5 || parts[5].equals("-")) {
                    history = "";
                }
                else{history = parts[5];}
                vehicles[i] = new Vehicle(id, type, capacity, history, pricePerKm, model);
            }
            catch (Exception e) {
                System.err.println("Error parsing line " + (i+1) + ": " + lines.get(i));
                e.printStackTrace();
            }
        }
        return vehicles;
    }
    //# id;xcoordinate;ycoordinate;name;type
    //1;25;25;Divoká Šárka;BUS
    public Place [] importPlaceData(String filePath) {
        List<String> lines = readDataFromFile(filePath);
        places = new Place[lines.size()];
        for (int i = 0; i < lines.size(); i++) {
            try{
                String[] parts = lines.get(i).split(";");
                for (int j = 0; j < parts.length; j++) {
                    parts[j] = parts[j].trim();
                }
                int id = Integer.parseInt(parts[0]);
                double x = Double.parseDouble(parts[1]);
                double y = Double.parseDouble(parts[2]);
                String name = parts[3];
                VehicleTypes type = VehicleTypes.valueOf(parts[4]);
                places[i] = new Stop(name, id, new Coordinates(x, y), type);
            }
            catch (Exception e) {
                System.err.println("Error parsing line " + (i+1) + ": " + lines.get(i));
                e.printStackTrace();
            }
        }
        return places;
    }
    //# first row contains TYPE of vehicle we define distances for
    //# second row list of stopsids in same order as is distance matrix, row coresponds to from, column to where
    //# then n*n matrix of distances between stops, it some distance is undefined write -1 
    //# then empty lines
    //# then n*n matrix of travel time between stops in seconds, it some distance is undefined write -1 
    public DistanceManager importDistanceData(String filePath) {
        List<String> lines = readDataFromFile(filePath);
        VehicleTypes type = VehicleTypes.valueOf(lines.get(0).trim());
        String[] stopIdsStr = lines.get(1).trim().split(";");
        int[] stopIds = new int[stopIdsStr.length];
        for (int i = 0; i < stopIdsStr.length; i++) { stopIds[i] = Integer.parseInt(stopIdsStr[i].trim()); }
        int [][] distanceMatrixMeters = new int[stopIds.length][stopIds.length];
        int [][] distanceMatrixSeconds = new int[stopIds.length][stopIds.length];
        for (int i = 0; i < stopIds.length; i++) {
            String[] distanceRow = lines.get(2 + i).trim().split(";");
            for (int j = 0; j < stopIds.length; j++) {
                distanceMatrixMeters[i][j] = Integer.parseInt(distanceRow[j].trim());
            }
        }
        for (int i = 0; i < stopIds.length; i++) {
            String[] timeRow = lines.get(2 + stopIds.length + i).trim().split(";");
            for (int j = 0; j < stopIds.length; j++) {
                distanceMatrixSeconds[i][j] = Integer.parseInt(timeRow[j].trim());
            }
        }
        Place[] places = findPlacesByIds(stopIds);
        

        return new DistanceManager(type, places, distanceMatrixMeters, distanceMatrixSeconds);
    }
    //#id of route ; list of stopsids divided by ; list of waiting time at each stop in seconds divided by;
    public Route [] importRouteData(String filePath) {
        List<String> lines = readDataFromFile(filePath);
        Route[] routes = new Route[lines.size()];
        for (int i = 0; i < lines.size(); i++) {
            try{
                String[] parts = lines.get(i).split(";");
                for (int j = 0; j < parts.length; j++) {
                    parts[j] = parts[j].trim();
                }
                int routeId = Integer.parseInt(parts[0]);
                int numStops = (parts.length - 1) / 2;
                int [] stopsIds = new int[numStops];
                int [] waitTimes = new int[numStops];
                for (int j = 0; j < numStops; j++) {
                    stopsIds[j] = Integer.parseInt(parts[1 + j]);
                }
                for (int j = 0; j < numStops; j++) {
                    waitTimes[j] = Integer.parseInt(parts[1 + numStops + j]);
                }
                Place[] routeStops = findPlacesByIds(stopsIds);
                routes[i] = new Route(routeId, routeStops, waitTimes);
                
            }
            catch (Exception e) {
                throw new IllegalArgumentException("Error parsing line " + (i+1) + ": " + lines.get(i), e);
            }
        }
        return routes;
    }
    //#ID vehicle; ID route; start time in 24h format hour:minute:second xx:xx:xx
    public ScheduledTrip [] importScheduleData(String filePath) {
        List<String> lines = readDataFromFile(filePath);
        ScheduledTrip[] schedule = new ScheduledTrip[lines.size()];
        for (int i = 0; i < lines.size(); i++) {
            try{
                String[] parts = lines.get(i).trim().split(";");
                for (int j = 0; j < parts.length; j++) {
                    parts[j] = parts[j].trim();
                }
                int vehicleId = Integer.parseInt(parts[0]);
                int routeId = Integer.parseInt(parts[1]);
                int startTimeSeconds = parseTimeToSeconds(parts[2]);
                // now we find objects matching its ids
                Vehicle vehicle = null;
                for (Vehicle v : vehicles) {
                    if (v.getId() == vehicleId) {
                        vehicle = v;
                        break;
                    }
                }
                if (vehicle == null) {
                    throw new IllegalArgumentException("Vehicle ID " + vehicleId + " not found in vehicles data!");
                }
                Route route = null;
                for (Route r : routes) {
                    if (r.getId() == routeId) {
                        route = r;
                        break;
                    }
                }
                if (route == null) {
                    throw new IllegalArgumentException("Route ID " + routeId + " not found in routes data!");
                }
                schedule[i] = new ScheduledTrip(vehicle, route, startTimeSeconds);
            }
            catch (Exception e) {
                throw new IllegalArgumentException("Error parsing line " + (i+1) + ": " + lines.get(i), e);
            }
        }
        return schedule;
    }

    public PassagerGeneration importPassagerGenerationData(String filePath) {
        List<String> lines = readDataFromFile(filePath);
        double [] averagePassengersAtStopPerminute = new double[lines.size()];
        int [] stopIds = new int[lines.size()];
        Place[] stops = new Place[lines.size()];
        for (int i = 0; i < lines.size(); i++) {
            try{
                String line = lines.get(i).trim();
                String [] parts = line.split(";");
                stopIds[i] = Integer.parseInt(parts[0].trim());
                averagePassengersAtStopPerminute[i] = Double.parseDouble(parts[1].trim());
            }
            catch (Exception e) {
                throw new IllegalArgumentException("Error parsing line " + (i+1) + ": " + lines.get(i), e);
            }
        }
        stops = findPlacesByIds(stopIds);
        return new PassagerGeneration(stops, averagePassengersAtStopPerminute);
    }
    
    public void importData(){
        vehicles = importVehicleData("data/vehicles.txt");
        places = importPlaceData("data/places.txt");
        distanceManager = new DistanceManager[1];
        distanceManager[0] = importDistanceData("data/distancesBus.txt");
        routes = importRouteData("data/routes.txt");
        schedule = importScheduleData("data/schedule.txt");
        passagerGeneration = importPassagerGenerationData("data/passengerGeneration.txt");
        placeById = new java.util.HashMap<>();
        for (Place p : places) {
            placeById.put(p.getId(), p);
        }
    }
    // write all getters for all data so we can access it from simulator
    public Vehicle[] getVehicles() {
        return vehicles;
    }
    public Place[] getPlaces() {
        return places;
    }
    public DistanceManager[] getDistanceManager() {
        return distanceManager;
    }
    public Route[] getRoutes() {
        return routes;
    }
    public ScheduledTrip[] getSchedule() {
        return schedule;
    }
    public PassagerGeneration getPassagerGeneration() {
        return passagerGeneration;
    }
}
