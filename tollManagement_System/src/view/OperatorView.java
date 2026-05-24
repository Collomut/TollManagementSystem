package view;

import controller.BoothController;
import controller.TransactionController;
import controller.VehicleController;
import model.TollBooth;
import model.Transaction;
import model.User;
import model.Vehicle;
import socket.TollClient;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class OperatorView extends JFrame {

    private TransactionController txController      = new TransactionController();
    private BoothController       boothController   = new BoothController();
    private VehicleController     vehicleController = new VehicleController();
    private TollClient            client;

    private JTextField txtPlateNumber;
    private JLabel lblVehicleInfo;
    private JLabel lblAmount;
    private JComboBox<String> cmbBooth;
    private JTable txTable;
    private DefaultTableModel txTableModel;

    private User loggedInUser;
    private List<TollBooth> booths;
    private Vehicle         foundVehicle;

    public OperatorView(User user) {
        this.loggedInUser = user;
        setTitle("Operator Panel — " + user.getFullName());
        setSize(700, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        
        client = new TollClient(message -> {
            System.out.println("Server says: " + message);
        });

       
        JPanel form = new JPanel(new GridLayout(6, 2, 5, 8));
        form.setBorder(BorderFactory.createTitledBorder("Process Toll Payment"));

        booths   = boothController.getAllBooths();
        cmbBooth = new JComboBox<>();
        for (TollBooth b : booths) cmbBooth.addItem(b.getBoothName());

        txtPlateNumber = new JTextField();
        lblVehicleInfo = new JLabel("— search a plate number first —");
        lblAmount      = new JLabel("—");

        JButton btnSearch = new JButton("Search Vehicle");

        form.add(new JLabel("Select Booth:"));      form.add(cmbBooth);
        form.add(new JLabel("Plate Number:"));      form.add(txtPlateNumber);
        form.add(new JLabel(""));                   form.add(btnSearch);
        form.add(new JLabel("Vehicle Found:"));     form.add(lblVehicleInfo);
        form.add(new JLabel("Amount to Charge:"));  form.add(lblAmount);

        JButton btnSave  = new JButton("Save Transaction");
        JButton btnClear = new JButton("Clear");
        JPanel  buttons  = new JPanel(new FlowLayout());
        buttons.add(btnSave);
        buttons.add(btnClear);
        form.add(new JLabel()); form.add(buttons);

        
        txTableModel = new DefaultTableModel(
            new String[]{"TXN ID", "Plate Number", "Booth", "Amount (RWF)", "Date", "Operator"}, 0);
        txTable = new JTable(txTableModel);
        txTable.setEnabled(false); // read only
        loadTransactions();

        add(form, BorderLayout.NORTH);
        add(new JScrollPane(txTable), BorderLayout.CENTER);

        
        btnSearch.addActionListener(e -> {
            String plate = txtPlateNumber.getText().trim().toUpperCase();

            if (plate.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a plate number.");
                return;
            }

            foundVehicle = vehicleController.getVehicleByPlate(plate);

            if (foundVehicle == null) {
                lblVehicleInfo.setText("Vehicle not found — ask owner to register first.");
                lblVehicleInfo.setForeground(Color.RED);
                lblAmount.setText("—");
            } else {
                lblVehicleInfo.setText("Found:  " + foundVehicle.getPlateNumber()
                    + "   |   Type: " + foundVehicle.getVehicleType());
                lblVehicleInfo.setForeground(new Color(0, 130, 0));

                double rate = txController.getRateByVehicleType(foundVehicle.getVehicleType());
                lblAmount.setText(rate + " RWF");
            }
        });

        
        btnSave.addActionListener(e -> {
            if (foundVehicle == null) {
                JOptionPane.showMessageDialog(this,
                    "Please search for a vehicle first.");
                return;
            }

            if (booths.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "No booths available. Ask admin to add a booth.");
                return;
            }

            int    boothIdx = cmbBooth.getSelectedIndex();
            int    boothId  = booths.get(boothIdx).getBoothId();
            double amount   = txController.getRateByVehicleType(
                                  foundVehicle.getVehicleType());

            Transaction t = new Transaction(
                foundVehicle.getVehicleId(), boothId, amount, user.getUserId());

            if (txController.addTransaction(t)) {
                JOptionPane.showMessageDialog(this,
                    "Transaction saved!\n"
                    + "Plate  : " + foundVehicle.getPlateNumber() + "\n"
                    + "Type   : " + foundVehicle.getVehicleType() + "\n"
                    + "Amount : " + amount + " RWF");

                
                client.sendMessage("NEW_TRANSACTION:" + user.getFullName()
                    + " collected " + amount + " RWF from "
                    + foundVehicle.getPlateNumber()
                    + " at booth " + booths.get(boothIdx).getBoothName());

                loadTransactions();
                clearForm();

            } else {
                JOptionPane.showMessageDialog(this, "Failed to save transaction.");
            }
        });

        
        btnClear.addActionListener(e -> clearForm());

        
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                client.disconnect();
            }
        });

        setVisible(true);
    }

   
    private void clearForm() {
        txtPlateNumber.setText("");
        lblVehicleInfo.setText("— search a plate number first —");
        lblVehicleInfo.setForeground(Color.BLACK);
        lblAmount.setText("—");
        foundVehicle = null;
        cmbBooth.setSelectedIndex(0);
    }

    private void loadTransactions() {
        txTableModel.setRowCount(0);
        for (Transaction t : txController.getAllTransactions()) {
            txTableModel.addRow(new Object[]{
                t.getTransactionId(),
                t.getPlateNumber(),
                t.getBoothName(),
                t.getAmountPaid(),
                t.getPaymentDate(),
                t.getOperatorName()
            });
        }
    }
}