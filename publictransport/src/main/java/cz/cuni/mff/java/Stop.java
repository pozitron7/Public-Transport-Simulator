package cz.cuni.mff.java;

public class Stop extends Place {
    VehicleTypes type;
    public Stop(String name, int id, Coordinates coordinates, VehicleTypes type) {
        super(name, id, coordinates);
        this.type = type;
    }
    public VehicleTypes getType() {
        return type;
    }
}
