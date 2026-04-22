package model;

public class TollRate {

    private int    rateId;
    private String vehicleType;   
    private double amount;

    
    public TollRate() {}

    public TollRate(String vehicleType, double amount) {
        this.vehicleType = vehicleType;
        this.amount      = amount;
    }

    
    public int getRateId()                  { return rateId; }
    public void setRateId(int id)           { this.rateId = id; }

    public String getVehicleType()          { return vehicleType; }
    public void setVehicleType(String t)    { this.vehicleType = t; }

    public double getAmount()               { return amount; }
    public void setAmount(double a)         { this.amount = a; }

    @Override
    public String toString() {
        return vehicleType + " → " + amount + " RWF";
    }
}