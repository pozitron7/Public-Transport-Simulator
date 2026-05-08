package cz.cuni.mff.java;

public abstract class Place {
    private String name;
    private String id;
    private Coordinates coordinates;

    public Place(String name, String id, Coordinates coordinates) {
        this.name = name;
        this.id = id;
        this.coordinates = coordinates;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }
}
