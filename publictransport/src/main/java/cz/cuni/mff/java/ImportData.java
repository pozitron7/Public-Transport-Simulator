package cz.cuni.mff.java;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ImportData {
    Vehicle[] vehicles;
    Place[] places;
    DistanceManager distanceManager;
    private List<String> readDataFromFile(String filePath) {
        try {
            List<String> lines = Files.readAllLines(Path.of(filePath));
            for (int i = 0; i < lines.size(); i++) {
                lines.set(i, lines.get(i).trim());
                if (lines.get(i).isEmpty() || lines.get(i).startsWith("#")) {
                    lines.remove(i);
                    i--;
                }
            }
            return lines;
        } catch (java.io.IOException e) {
            e.printStackTrace();
            return List.of();
        }
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

}
