package cz.cuni.mff.java;

public class DistanceManager {
    VehicleTypes type;
    Place[] places;
    int [][] distanceMatrixSeconds;
    int [][] distanceMatrixMeters;
    /**
 * Calculates the estimated time of arrival based on current speed.
 * * @param places list of places or stops, their order corresponds to the distance matrix.
 * @param distanceMatrixSeconds The distance matrix in seconds.
 * @param distanceMatrixMeters The distance matrix in meters rows corespond to from, columns to so we index like distanceMatrixMeters[fromIndex][toIndex].
 * distance matricies may contain -1 for indefined distances
    */
    public DistanceManager(VehicleTypes type, Place[] places, int[][] distanceMatrixSeconds, int[][] distanceMatrixMeters) {
        //distanceMatrixMeters[fromIndex][toIndex]
        this.type = type;
        this.places = places;
        this.distanceMatrixSeconds = distanceMatrixSeconds;
        this.distanceMatrixMeters = distanceMatrixMeters;
    }
    private int getPlaceIndex(Place place) {
        for (int i = 0; i < places.length; i++) {
            if (places[i].equals(place)) {
                return i;
            }
        }
        throw new IllegalArgumentException("Place not found: " + place);
    }
    public int getDistanceSeconds(Place from, Place to) {
        int fromIndex = getPlaceIndex(from);
        int toIndex = getPlaceIndex(to);
        return distanceMatrixSeconds[fromIndex][toIndex];
    }
    public int getDistanceMeters(Place from, Place to) {
        int fromIndex = getPlaceIndex(from);
        int toIndex = getPlaceIndex(to);
        return distanceMatrixMeters[fromIndex][toIndex];
    }
    public VehicleTypes getType() {
        return type;
    }
}
