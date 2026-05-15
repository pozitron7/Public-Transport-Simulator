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
@Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        Place place = (Place) other;
        return this.id == place.id;
    }
    public abstract VehicleTypes getType();
}
