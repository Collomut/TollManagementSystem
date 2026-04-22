package view;

import controller.TransactionController;
import controller.VehicleController;
import model.Transaction;
import model.User;
import model.Vehicle;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class OwnerView extends JFrame {

    private VehicleController     vehicleController = new VehicleController();
    private TransactionController txController      = new TransactionController();

    private JTextField        txtPlate;
    private JComboBox<String> cmbType;
    private DefaultTableModel vehicleTableModel;
    private DefaultTableModel txTableModel;

    private User loggedInUser;

    public OwnerView(User user) {
        this.loggedInUser = user;
        setTitle("Owner Panel — " + user.getFullName());
        setSize(650, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.add("My Vehicles",    buildVehiclePanel());
        tabs.add("My Transactions", buildTxPanel());

        add(tabs);
        setVisible(true);
    }

    
    private JPanel buildVehiclePanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel form = new JPanel(new GridLayout(3, 2, 5, 5));
        txtPlate = new JTextField();
        cmbType  = new JComboBox<>(new String[]{"car","truck","motorbike","bus"});

        form.add(new JLabel("Plate Number:")); form.add(txtPlate);
        form.add(new JLabel("Vehicle Type:")); form.add(cmbType);

        JButton btnAdd    = new JButton("Register");
        JButton btnDelete = new JButton("Delete");
        JButton btnClear  = new JButton("Clear");
        JPanel  buttons   = new JPanel(new FlowLayout());
        buttons.add(btnAdd); buttons.add(btnDelete); buttons.add(btnClear);
        form.add(new JLabel()); form.add(buttons);

        vehicleTableModel = new DefaultTableModel(
            new String[]{"ID", "Plate Number", "Type"}, 0);
        JTable vehicleTable = new JTable(vehicleTableModel);
        loadVehicles();

        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(vehicleTable), BorderLayout.CENTER);

        btnAdd.addActionListener(e -> {
            Vehicle v = new Vehicle(txtPlate.getText().trim(),
                                    (String) cmbType.getSelectedItem(),
                                    loggedInUser.getUserId());
            if (vehicleController.addVehicle(v)) {
                JOptionPane.showMessageDialog(this, "Vehicle registered.");
                loadVehicles(); txtPlate.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Failed. Plate may already exist.");
            }
        });

        btnDelete.addActionListener(e -> {
            int row = vehicleTable.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Select a vehicle first."); return; }
            int id = (int) vehicleTableModel.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "Delete this vehicle?");
            if (confirm == JOptionPane.YES_OPTION) {
                vehicleController.deleteVehicle(id);
                loadVehicles();
            }
        });

        btnClear.addActionListener(e -> txtPlate.setText(""));

        vehicleTable.getSelectionModel().addListSelectionListener(e -> {
            int row = vehicleTable.getSelectedRow();
            if (row >= 0) txtPlate.setText((String) vehicleTableModel.getValueAt(row, 1));
        });

        return panel;
    }

    
    private JPanel buildTxPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        txTableModel = new DefaultTableModel(
            new String[]{"TXN ID", "Vehicle ID", "Booth ID", "Amount (RWF)", "Date"}, 0);
        JTable txTable = new JTable(txTableModel);

        JButton btnRefresh = new JButton("Refresh");
        loadTxHistory();

        panel.add(btnRefresh, BorderLayout.NORTH);
        panel.add(new JScrollPane(txTable), BorderLayout.CENTER);

        btnRefresh.addActionListener(e -> loadTxHistory());
        return panel;
    }

    private void loadVehicles() {
        vehicleTableModel.setRowCount(0);
        for (Vehicle v : vehicleController.getVehiclesByOwner(loggedInUser.getUserId())) {
            vehicleTableModel.addRow(new Object[]{
                v.getVehicleId(), v.getPlateNumber(), v.getVehicleType()
            });
        }
    }

    private void loadTxHistory() {
        txTableModel.setRowCount(0);
        for (Transaction t : txController.getTransactionsByOwner(loggedInUser.getUserId())) {
            txTableModel.addRow(new Object[]{
                t.getTransactionId(), t.getVehicleId(), t.getBoothId(),
                t.getAmountPaid(), t.getPaymentDate()
            });
        }
    }
}