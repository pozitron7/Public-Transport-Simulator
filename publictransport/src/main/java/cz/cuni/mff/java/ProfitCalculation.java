package cz.cuni.mff.java;

public class ProfitCalculation {
    int numberOfTransportedPassagers = 0;
    int averagePriceOfTicket = 20;
    double moneyMade = 0;
    double moneySpent = 0;
    double profit;
    public double calculateBalanceAterSimulation(VehicleInSimulation [] vehicles){ // vehicles has stored info about journeys
        for (VehicleInSimulation vehicle : vehicles) {
            numberOfTransportedPassagers += vehicle.getNumberOfTransportedPassagers();
            moneySpent += vehicle.getDistanceTraveledMeters() * vehicle.getPricePerKm() /1000;
        }
        moneyMade = numberOfTransportedPassagers * averagePriceOfTicket;
        profit = moneyMade - moneySpent;
        return profit;
        
    }
}
