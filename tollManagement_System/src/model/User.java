package model;

import java.sql.Timestamp;

public class User {

    private int userId;
    private String username;
    private String password;
    private String fullName;
    private String role;         
    private Timestamp createdAt;

    
    public User() {}

    public User(String username, String password, String fullName, String role) {
        this.username  = username;
        this.password  = password;
        this.fullName  = fullName;
        this.role      = role;
    }

    
    public int getUserId(){
    	return userId;
    	}
    public void setUserId(int userId){
    	this.userId = userId;
    	}
    public String getUsername(){
    	return username;
    	}
    public void setUsername(String u){
    	this.username = u;
    	}
    public String getPassword(){
    	return password;
    	}
    public void setPassword(String p){
    	this.password = p;
    	}
    public String getFullName(){
    	return fullName;
    	}
    public void setFullName(String f){
    	this.fullName = f;
    	}
    public String getRole(){
    	return role;
    	}
    public void setRole(String r){
    	this.role = r;
    	}
    public Timestamp getCreatedAt(){
    	return createdAt;
    	}
    public void setCreatedAt(Timestamp t){
    	this.createdAt = t; 
    	}
    
    @Override
    public String toString() {
        return fullName + " [" + role + "]";
    }
}