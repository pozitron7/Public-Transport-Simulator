package cz.cuni.mff.java;

public abstract class Place {
    private String name;
    private int id;
    private Coordinates coordinates;

    public Place(String name, int id, Coordinates coordinates) {
        this.name = name;
        this.id = id;
        this.coordinates = coordinates;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }
    public boolean equals(Place other) {
        return this.id == other.id;
    }
    public abstract VehicleTypes getType();
}
