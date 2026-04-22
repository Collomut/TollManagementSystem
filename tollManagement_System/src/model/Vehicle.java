package model;

import java.sql.Timestamp;

public class Vehicle {

    private int       vehicleId;
    private String    plateNumber;
    private String    vehicleType;   
    private int       ownerId;
    private Timestamp registeredAt;

    
    public Vehicle() {}

    public Vehicle(String plateNumber, String vehicleType, int ownerId) {
        this.plateNumber = plateNumber;
        this.vehicleType = vehicleType;
        this.ownerId     = ownerId;
    }

   
    public int getVehicleId()                   { return vehicleId; }
    public void setVehicleId(int id)            { this.vehicleId = id; }

    public String getPlateNumber()              { return plateNumber; }
    public void setPlateNumber(String p)        { this.plateNumber = p; }

    public String getVehicleType()              { return vehicleType; }
    public void setVehicleType(String t)        { this.vehicleType = t; }

    public int getOwnerId()                     { return ownerId; }
    public void setOwnerId(int id)              { this.ownerId = id; }

    public Timestamp getRegisteredAt()          { return registeredAt; }
    public void setRegisteredAt(Timestamp t)    { this.registeredAt = t; }

    @Override
    public String toString() {
        return plateNumber + " (" + vehicleType + ")";
    }
}