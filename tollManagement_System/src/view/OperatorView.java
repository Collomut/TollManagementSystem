package view;

import controller.BoothController;
import controller.TransactionController;
import model.TollBooth;
import model.Transaction;
import model.User;
import socket.TollClient;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class OperatorView extends JFrame {

    private TransactionController txController    = new TransactionController();
    private BoothController       boothController = new BoothController();
    private TollClient            client;

    private JTextField        txtVehicleId, txtAmount;
    private JComboBox<String> cmbBooth;
    private JTable            txTable;
    private DefaultTableModel txTableModel;

    private User           loggedInUser;
    private List<TollBooth> booths;

    public OperatorView(User user) {
        this.loggedInUser = user;
        setTitle("Operator Panel — " + user.getFullName());
        setSize(600, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        
        client = new TollClient(message -> {
            
            System.out.println("Server says: " + message);
        });

        
        JPanel form = new JPanel(new GridLayout(4, 2, 5, 5));
        form.setBorder(BorderFactory.createTitledBorder("Process Payment"));

        booths   = boothController.getAllBooths();
        cmbBooth = new JComboBox<>();
        for (TollBooth b : booths) cmbBooth.addItem(b.getBoothName());

        txtVehicleId = new JTextField();
        txtAmount    = new JTextField();

        form.add(new JLabel("Select Booth:"));  form.add(cmbBooth);
        form.add(new JLabel("Vehicle ID:"));    form.add(txtVehicleId);
        form.add(new JLabel("Amount (RWF):"));  form.add(txtAmount);

        JButton btnSave  = new JButton("Save Transaction");
        JButton btnClear = new JButton("Clear");
        JPanel  buttons  = new JPanel(new FlowLayout());
        buttons.add(btnSave); buttons.add(btnClear);
        form.add(new JLabel()); form.add(buttons);

        
        txTableModel = new DefaultTableModel(
            new String[]{"TXN ID", "Vehicle ID", "Booth ID", "Amount", "Date"}, 0);
        txTable = new JTable(txTableModel);
        loadTransactions();

        add(form, BorderLayout.NORTH);
        add(new JScrollPane(txTable), BorderLayout.CENTER);

        
        btnSave.addActionListener(e -> {
            try {
                int    vehicleId = Integer.parseInt(txtVehicleId.getText().trim());
                double amount    = Double.parseDouble(txtAmount.getText().trim());
                int    boothIdx  = cmbBooth.getSelectedIndex();
                int    boothId   = booths.get(boothIdx).getBoothId();

                Transaction t = new Transaction(vehicleId, boothId, amount, user.getUserId());

                if (txController.addTransaction(t)) {
                    JOptionPane.showMessageDialog(this, "Transaction saved.");
                    loadTransactions();
                    txtVehicleId.setText(""); txtAmount.setText("");

                    
                    client.sendMessage("NEW_TRANSACTION:" + user.getFullName()
                        + " processed " + amount + " RWF at booth " + boothId);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Vehicle ID and Amount must be numbers.");
            }
        });

        btnClear.addActionListener(e -> {
            txtVehicleId.setText(""); txtAmount.setText("");
        });

        
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                client.disconnect();
            }
        });

        setVisible(true);
    }

    private void loadTransactions() {
        txTableModel.setRowCount(0);
        for (Transaction t : txController.getAllTransactions()) {
            txTableModel.addRow(new Object[]{
                t.getTransactionId(), t.getVehicleId(), t.getBoothId(),
                t.getAmountPaid(), t.getPaymentDate()
            });
        }
    }
}