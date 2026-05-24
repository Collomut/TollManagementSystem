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
        setSize(750, 470);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

       
        client = new TollClient(message -> {
            if (message.startsWith("NEW_TRANSACTION:")) {
                String info = message.replace("NEW_TRANSACTION:", "");
                SwingUtilities.invokeLater(() -> {
                    lblAlert.setText("Live update: " + info);
                    lblAlert.setForeground(new Color(0, 130, 0));
                    loadData();
                });
            }
        });

        
        lblRevenue = new JLabel("Total Revenue: Loading...");
        lblRevenue.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        lblRevenue.setFont(new Font("Arial", Font.BOLD, 13));

        JButton btnRefresh = new JButton("Refresh");

        JPanel top = new JPanel(new BorderLayout());
        top.add(lblRevenue, BorderLayout.WEST);
        top.add(btnRefresh, BorderLayout.EAST);

        
        lblAlert = new JLabel("  Waiting for new transactions...");
        lblAlert.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));

        JPanel north = new JPanel(new GridLayout(2, 1));
        north.add(top);
        north.add(lblAlert);

        
        tableModel = new DefaultTableModel(
            new String[]{"TXN ID", "Plate Number", "Booth", "Amount (RWF)", "Date", "Operator"}, 0);
        JTable table = new JTable(tableModel);
        table.setEnabled(false); 

        loadData();

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
                t.getTransactionId(),
                t.getPlateNumber(),
                t.getBoothName(),
                t.getAmountPaid(),
                t.getPaymentDate(),
                t.getOperatorName()
            });
        }
        lblRevenue.setText("Total Revenue: " + txController.getTotalRevenue() + " RWF");
    }
}