package model;

import java.sql.Timestamp;

public class Transaction {

    private int       transactionId;
    private int       vehicleId;
    private int       boothId;
    private double    amountPaid;
    private Timestamp paymentDate;
    private int       processedBy;

    
    private String plateNumber;
    private String boothName;
    private String operatorName;

    
    public Transaction() {}

    public Transaction(int vehicleId, int boothId, double amountPaid, int processedBy) {
        this.vehicleId   = vehicleId;
        this.boothId     = boothId;
        this.amountPaid  = amountPaid;
        this.processedBy = processedBy;
    }

    
    public int getTransactionId(){
    	return transactionId;
    	}
    public void setTransactionId(int id){
    	this.transactionId = id;
    	}
    public int getVehicleId(){
    	return vehicleId;
    	}
    public void setVehicleId(int id){
    	this.vehicleId = id;
    	}
    public int getBoothId(){
    	return boothId;
    	}
    public void setBoothId(int id){
    	this.boothId = id;
    	}
    public double getAmountPaid(){
    	return amountPaid; 
    	}
    public void setAmountPaid(double a) {
    	this.amountPaid = a;
    	}
    public Timestamp getPaymentDate(){
    	return paymentDate;
    	}
    public void setPaymentDate(Timestamp t){
    	this.paymentDate = t;
    	}
    public int getProcessedBy(){
    	return processedBy;
    	}
    public void setProcessedBy(int id){
    	this.processedBy = id;
    	}
    public String getPlateNumber(){
    	return plateNumber;
    	}
    public void setPlateNumber(String p){
    	this.plateNumber = p;
    	}
    public String getBoothName(){
    	return boothName;
    	}
    public void setBoothName(String b){
    	this.boothName = b; 
    	}
    public String getOperatorName(){
    	return operatorName;
    	}
    public void setOperatorName(String o){
    	this.operatorName = o;
    	}

    @Override
    public String toString() {
        return "TXN#" + transactionId + " | " + plateNumber
             + " | " + amountPaid + " RWF";
    }
}