package view;

import controller.BoothController;
import controller.UserController;
import model.TollBooth;
import model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminView extends JFrame {

    private UserController  userController  = new UserController();
    private BoothController boothController = new BoothController();

   
    private JTextField txtFullName, txtUsername, txtPassword;
    private JComboBox<String> cmbRole;
    private JTable     userTable;
    private DefaultTableModel userTableModel;

    
    private JTextField txtBoothName, txtLocation, txtOperatorId;
    private JTable     boothTable;
    private DefaultTableModel boothTableModel;

    private User loggedInUser;

    public AdminView(User user) {
        this.loggedInUser = user;
        setTitle("Admin Dashboard — " + user.getFullName());
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.add("Manage Users",  buildUserPanel());
        tabs.add("Manage Booths", buildBoothPanel());

        add(tabs);
        setVisible(true);
    }

    
    private JPanel buildUserPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        
        JPanel form = new JPanel(new GridLayout(5, 2, 5, 5));
        txtFullName = new JTextField();
        txtUsername = new JTextField();
        txtPassword = new JTextField();
        cmbRole     = new JComboBox<>(new String[]{"admin","operator","supervisor","owner"});

        form.add(new JLabel("Full Name:"));  form.add(txtFullName);
        form.add(new JLabel("Username:"));   form.add(txtUsername);
        form.add(new JLabel("Password:"));   form.add(txtPassword);
        form.add(new JLabel("Role:"));       form.add(cmbRole);

        
        JPanel buttons = new JPanel(new FlowLayout());
        JButton btnAdd    = new JButton("Add");
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");
        JButton btnClear  = new JButton("Clear");
        buttons.add(btnAdd);
        buttons.add(btnUpdate);
        buttons.add(btnDelete);
        buttons.add(btnClear);
        form.add(buttons);

        
        userTableModel = new DefaultTableModel(
            new String[]{"ID", "Full Name", "Username", "Role"}, 0);
        userTable = new JTable(userTableModel);
        loadUserTable();

        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(userTable), BorderLayout.CENTER);

        
        btnAdd.addActionListener(e -> {
            User u = new User(txtUsername.getText(), txtPassword.getText(),
                              txtFullName.getText(), (String) cmbRole.getSelectedItem());
            if (userController.addUser(u)) {
                JOptionPane.showMessageDialog(this, "User added.");
                loadUserTable(); clearUserForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add user.");
            }
        });

        btnUpdate.addActionListener(e -> {
            int row = userTable.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Select a user first."); return; }
            User u = new User();
            u.setUserId((int) userTableModel.getValueAt(row, 0));
            u.setFullName(txtFullName.getText());
            u.setPassword(txtPassword.getText());
            u.setRole((String) cmbRole.getSelectedItem());
            if (userController.updateUser(u)) {
                JOptionPane.showMessageDialog(this, "User updated.");
                loadUserTable(); clearUserForm();
            }
        });

        btnDelete.addActionListener(e -> {
            int row = userTable.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Select a user first."); return; }
            int id = (int) userTableModel.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "Delete this user?");
            if (confirm == JOptionPane.YES_OPTION) {
                userController.deleteUser(id);
                loadUserTable(); clearUserForm();
            }
        });

        btnClear.addActionListener(e -> clearUserForm());

        
        userTable.getSelectionModel().addListSelectionListener(e -> {
            int row = userTable.getSelectedRow();
            if (row >= 0) {
                txtFullName.setText((String) userTableModel.getValueAt(row, 1));
                txtUsername.setText((String) userTableModel.getValueAt(row, 2));
                cmbRole.setSelectedItem(userTableModel.getValueAt(row, 3));
            }
        });

        return panel;
    }

    private void loadUserTable() {
        userTableModel.setRowCount(0);
        List<User> users = userController.getAllUsers();
        for (User u : users) {
            userTableModel.addRow(new Object[]{
                u.getUserId(), u.getFullName(), u.getUsername(), u.getRole()
            });
        }
    }

    private void clearUserForm() {
        txtFullName.setText(""); txtUsername.setText(""); txtPassword.setText("");
        cmbRole.setSelectedIndex(0); userTable.clearSelection();
    }

    
    private JPanel buildBoothPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel form = new JPanel(new GridLayout(4, 2, 5, 5));
        txtBoothName   = new JTextField();
        txtLocation    = new JTextField();
        txtOperatorId  = new JTextField();

        form.add(new JLabel("Booth Name:"));    form.add(txtBoothName);
        form.add(new JLabel("Location:"));      form.add(txtLocation);
        form.add(new JLabel("Operator ID:"));   form.add(txtOperatorId);

        JPanel buttons = new JPanel(new FlowLayout());
        JButton btnAdd    = new JButton("Add");
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");
        JButton btnClear  = new JButton("Clear");
        buttons.add(btnAdd); buttons.add(btnUpdate);
        buttons.add(btnDelete); buttons.add(btnClear);
        form.add(buttons);

        boothTableModel = new DefaultTableModel(
            new String[]{"ID", "Booth Name", "Location", "Operator ID"}, 0);
        boothTable = new JTable(boothTableModel);
        loadBoothTable();

        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(boothTable), BorderLayout.CENTER);

        btnAdd.addActionListener(e -> {
            try {
                TollBooth b = new TollBooth(txtBoothName.getText(), txtLocation.getText(),
                                            Integer.parseInt(txtOperatorId.getText()));
                if (boothController.addBooth(b)) {
                    JOptionPane.showMessageDialog(this, "Booth added.");
                    loadBoothTable(); clearBoothForm();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Operator ID must be a number.");
            }
        });

        btnUpdate.addActionListener(e -> {
            int row = boothTable.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Select a booth first."); return; }
            try {
                TollBooth b = new TollBooth();
                b.setBoothId((int) boothTableModel.getValueAt(row, 0));
                b.setBoothName(txtBoothName.getText());
                b.setLocation(txtLocation.getText());
                b.setAssignedOperator(Integer.parseInt(txtOperatorId.getText()));
                if (boothController.updateBooth(b)) {
                    JOptionPane.showMessageDialog(this, "Booth updated.");
                    loadBoothTable(); clearBoothForm();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Operator ID must be a number.");
            }
        });

        btnDelete.addActionListener(e -> {
            int row = boothTable.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Select a booth first."); return; }
            int id = (int) boothTableModel.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "Delete this booth?");
            if (confirm == JOptionPane.YES_OPTION) {
                boothController.deleteBooth(id);
                loadBoothTable(); clearBoothForm();
            }
        });

        btnClear.addActionListener(e -> clearBoothForm());

        boothTable.getSelectionModel().addListSelectionListener(e -> {
            int row = boothTable.getSelectedRow();
            if (row >= 0) {
                txtBoothName.setText((String)  boothTableModel.getValueAt(row, 1));
                txtLocation.setText((String)   boothTableModel.getValueAt(row, 2));
                txtOperatorId.setText(String.valueOf(boothTableModel.getValueAt(row, 3)));
            }
        });

        return panel;
    }

    private void loadBoothTable() {
        boothTableModel.setRowCount(0);
        for (TollBooth b : boothController.getAllBooths()) {
            boothTableModel.addRow(new Object[]{
                b.getBoothId(), b.getBoothName(), b.getLocation(), b.getAssignedOperator()
            });
        }
    }

    private void clearBoothForm() {
        txtBoothName.setText(""); txtLocation.setText(""); txtOperatorId.setText("");
        boothTable.clearSelection();
    }
}