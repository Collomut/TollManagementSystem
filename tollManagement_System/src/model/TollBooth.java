package model;

public class TollBooth {

    private int    boothId;
    private String boothName;
    private String location;
    private int    assignedOperator;  

    
    public TollBooth() {}

    public TollBooth(String boothName, String location, int assignedOperator) {
        this.boothName        = boothName;
        this.location         = location;
        this.assignedOperator = assignedOperator;
    }

    
    public int getBoothId()                         { return boothId; }
    public void setBoothId(int id)                  { this.boothId = id; }

    public String getBoothName()                    { return boothName; }
    public void setBoothName(String name)           { this.boothName = name; }

    public String getLocation()                     { return location; }
    public void setLocation(String loc)             { this.location = loc; }

    public int getAssignedOperator()                { return assignedOperator; }
    public void setAssignedOperator(int opId)       { this.assignedOperator = opId; }

    @Override
    public String toString() {
        return boothName + " — " + location;
    }
}