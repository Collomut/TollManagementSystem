package view;

import controller.TransactionController;
import model.Transaction;
import model.User;
import socket.TollClient;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class SupervisorView extends JFrame {

    private TransactionController txController = new TransactionController();
    private DefaultTableModel     tableModel;
    private JLabel                lblRevenue;
    private JLabel                lblAlert;
    private TollClient            client;

    public SupervisorView(User user) {
        setTitle("Supervisor Panel — " + user.getFullName());
        setSize(670, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        
        client = new TollClient(message -> {
            if (message.startsWith("NEW_TRANSACTION:")) {
                String info = message.replace("NEW_TRANSACTION:", "");
                
                SwingUtilities.invokeLater(() -> {
                    lblAlert.setText("🔔 Live update: " + info);
                    loadData(); 
                });
            }
        });

        
        lblRevenue = new JLabel("Total Revenue: Loading...");
        lblRevenue.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JButton btnRefresh = new JButton("Refresh");
        JPanel  top = new JPanel(new BorderLayout());
        top.add(lblRevenue, BorderLayout.WEST);
        top.add(btnRefresh, BorderLayout.EAST);

        
        lblAlert = new JLabel("  Waiting for transactions...");
        lblAlert.setForeground(Color.BLUE);
        lblAlert.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));

        
        tableModel = new DefaultTableModel(
            new String[]{"TXN ID", "Vehicle ID", "Booth ID", "Amount (RWF)", "Date", "Operator ID"}, 0);
        JTable table = new JTable(tableModel);
        loadData();

        JPanel north = new JPanel(new GridLayout(2, 1));
        north.add(top);
        north.add(lblAlert);

        add(north, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        btnRefresh.addActionListener(e -> loadData());

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                client.disconnect();
            }
        });

        setVisible(true);
    }

    private void loadData() {
        tableModel.setRowCount(0);
        for (Transaction t : txController.getAllTransactions()) {
            tableModel.addRow(new Object[]{
                t.getTransactionId(), t.getVehicleId(), t.getBoothId(),
                t.getAmountPaid(), t.getPaymentDate(), t.getProcessedBy()
            });
        }
        lblRevenue.setText("Total Revenue: " + txController.getTotalRevenue() + " RWF");
    }
}