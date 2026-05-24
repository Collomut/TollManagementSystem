package controller;

import database.DBConnection;
import model.Transaction;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionController {

    
    public boolean addTransaction(Transaction t) {
        String sql = "INSERT INTO transactions (vehicle_id, booth_id, amount_paid, processed_by) "
                   + "VALUES (?, ?, ?, ?)";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, t.getVehicleId());
            ps.setInt(2, t.getBoothId());
            ps.setDouble(3, t.getAmountPaid());
            ps.setInt(4, t.getProcessedBy());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Add transaction error: " + e.getMessage());
            return false;
        }
    }

    
    public List<Transaction> getAllTransactions() {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT t.transaction_id, v.plate_number, b.booth_name, "
                   + "t.amount_paid, t.payment_date, u.full_name "
                   + "FROM transactions t "
                   + "JOIN vehicles v   ON t.vehicle_id   = v.vehicle_id "
                   + "JOIN toll_booths b ON t.booth_id    = b.booth_id "
                   + "JOIN users u       ON t.processed_by = u.user_id "
                   + "ORDER BY t.payment_date DESC";
        try {
            Connection conn = DBConnection.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                Transaction t = new Transaction();
                t.setTransactionId(rs.getInt("transaction_id"));
                t.setAmountPaid(rs.getDouble("amount_paid"));
                t.setPaymentDate(rs.getTimestamp("payment_date"));
                t.setPlateNumber(rs.getString("plate_number"));
                t.setBoothName(rs.getString("booth_name"));
                t.setOperatorName(rs.getString("full_name"));
                list.add(t);
            }
        } catch (Exception e) {
            System.out.println("Get transactions error: " + e.getMessage());
        }
        return list;
    }

    
    public List<Transaction> getTransactionsByOwner(int ownerId) {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT t.transaction_id, v.plate_number, b.booth_name, "
                   + "t.amount_paid, t.payment_date "
                   + "FROM transactions t "
                   + "JOIN vehicles v    ON t.vehicle_id = v.vehicle_id "
                   + "JOIN toll_booths b ON t.booth_id   = b.booth_id "
                   + "WHERE v.owner_id = ? "
                   + "ORDER BY t.payment_date DESC";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, ownerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Transaction t = new Transaction();
                t.setTransactionId(rs.getInt("transaction_id"));
                t.setAmountPaid(rs.getDouble("amount_paid"));
                t.setPaymentDate(rs.getTimestamp("payment_date"));
                t.setPlateNumber(rs.getString("plate_number"));
                t.setBoothName(rs.getString("booth_name"));
                list.add(t);
            }
        } catch (Exception e) {
            System.out.println("Get transactions by owner error: " + e.getMessage());
        }
        return list;
    }

    
    public double getRateByVehicleType(String vehicleType) {
        String sql = "SELECT amount FROM toll_rates WHERE vehicle_type = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, vehicleType);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble("amount");
        } catch (Exception e) {
            System.out.println("Get rate error: " + e.getMessage());
        }
        return 0;
    }

    
    public double getTotalRevenue() {
        String sql = "SELECT SUM(amount_paid) FROM transactions";
        try {
            Connection conn = DBConnection.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            if (rs.next()) return rs.getDouble(1);
        } catch (Exception e) {
            System.out.println("Revenue error: " + e.getMessage());
        }
        return 0;
    }
}