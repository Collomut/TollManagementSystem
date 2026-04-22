package controller;

import database.DBConnection;
import model.Vehicle;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VehicleController {

    
    public boolean addVehicle(Vehicle vehicle) {
        String sql = "INSERT INTO vehicles (plate_number, vehicle_type, owner_id) VALUES (?, ?, ?)";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, vehicle.getPlateNumber());
            ps.setString(2, vehicle.getVehicleType());
            ps.setInt(3, vehicle.getOwnerId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Add vehicle error: " + e.getMessage());
            return false;
        }
    }

    
    public List<Vehicle> getAllVehicles() {
        List<Vehicle> list = new ArrayList<>();
        String sql = "SELECT * FROM vehicles";
        try {
            Connection conn = DBConnection.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                Vehicle v = new Vehicle();
                v.setVehicleId(rs.getInt("vehicle_id"));
                v.setPlateNumber(rs.getString("plate_number"));
                v.setVehicleType(rs.getString("vehicle_type"));
                v.setOwnerId(rs.getInt("owner_id"));
                list.add(v);
            }
        } catch (Exception e) {
            System.out.println("Get vehicles error: " + e.getMessage());
        }
        return list;
    }

    
    public List<Vehicle> getVehiclesByOwner(int ownerId) {
        List<Vehicle> list = new ArrayList<>();
        String sql = "SELECT * FROM vehicles WHERE owner_id = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, ownerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Vehicle v = new Vehicle();
                v.setVehicleId(rs.getInt("vehicle_id"));
                v.setPlateNumber(rs.getString("plate_number"));
                v.setVehicleType(rs.getString("vehicle_type"));
                v.setOwnerId(rs.getInt("owner_id"));
                list.add(v);
            }
        } catch (Exception e) {
            System.out.println("Get vehicles by owner error: " + e.getMessage());
        }
        return list;
    }

    
    public boolean updateVehicle(Vehicle vehicle) {
        String sql = "UPDATE vehicles SET plate_number=?, vehicle_type=? WHERE vehicle_id=?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, vehicle.getPlateNumber());
            ps.setString(2, vehicle.getVehicleType());
            ps.setInt(3, vehicle.getVehicleId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Update vehicle error: " + e.getMessage());
            return false;
        }
    }

    
    public boolean deleteVehicle(int vehicleId) {
        String sql = "DELETE FROM vehicles WHERE vehicle_id = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, vehicleId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Delete vehicle error: " + e.getMessage());
            return false;
        }
    }
}