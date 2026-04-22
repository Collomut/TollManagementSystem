package controller;

import database.DBConnection;
import model.TollBooth;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BoothController {

    public boolean addBooth(TollBooth booth) {
        String sql = "INSERT INTO toll_booths (booth_name, location, assigned_operator) VALUES (?, ?, ?)";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, booth.getBoothName());
            ps.setString(2, booth.getLocation());
            ps.setInt(3, booth.getAssignedOperator());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Add booth error: " + e.getMessage());
            return false;
        }
    }

    public List<TollBooth> getAllBooths() {
        List<TollBooth> list = new ArrayList<>();
        String sql = "SELECT * FROM toll_booths";
        try {
            Connection conn = DBConnection.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                TollBooth b = new TollBooth();
                b.setBoothId(rs.getInt("booth_id"));
                b.setBoothName(rs.getString("booth_name"));
                b.setLocation(rs.getString("location"));
                b.setAssignedOperator(rs.getInt("assigned_operator"));
                list.add(b);
            }
        } catch (Exception e) {
            System.out.println("Get booths error: " + e.getMessage());
        }
        return list;
    }

    public boolean updateBooth(TollBooth booth) {
        String sql = "UPDATE toll_booths SET booth_name=?, location=?, assigned_operator=? WHERE booth_id=?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, booth.getBoothName());
            ps.setString(2, booth.getLocation());
            ps.setInt(3, booth.getAssignedOperator());
            ps.setInt(4, booth.getBoothId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Update booth error: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteBooth(int boothId) {
        String sql = "DELETE FROM toll_booths WHERE booth_id = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, boothId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Delete booth error: " + e.getMessage());
            return false;
        }
    }
}