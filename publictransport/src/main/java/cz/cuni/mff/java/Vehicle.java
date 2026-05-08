package cz.cuni.mff.java;

public class Vehicle {
    private int id;
    private VehicleTypes type;
    private String model;
    private int capacity;
    private String history;
    private int pricePerKm;
    public Vehicle(int id, VehicleTypes type, int capacity, String history, int pricePerKm, String model) {
        this.id = id;
        this.type = type;
        this.capacity = capacity;
        this.model = model;
        this.history = history;
        this.pricePerKm = pricePerKm;
    }
    public Vehicle(int id, VehicleTypes type, int capacity, String history, int pricePerKm) {
        this.id = id;
        this.type = type;
        this.capacity = capacity;
        this.model = "Unknown";
        this.history = history;
        this.pricePerKm = pricePerKm;
    }
    public void addHistory(String history) {
        this.history += history;
    }
    public int getId() {
        return id;
    }
    public VehicleTypes getType() {
        return type;
    }
    public int getCapacity() {
        return capacity;
    }
    public String getModel() {
        return model;
    }
    public String getHistory() {
        return history;
    }
    public int getPricePerKm() {
        return pricePerKm;
    }
}
