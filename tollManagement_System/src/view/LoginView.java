package view;

import controller.AuthController;
import model.User;

import javax.swing.*;
import java.awt.*;

public class LoginView extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton        btnLogin;
    private AuthController authController = new AuthController();

    public LoginView() {
        setTitle("Toll Management System — Login");
        setSize(350, 220);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("Username:"));
        txtUsername = new JTextField();
        panel.add(txtUsername);

        panel.add(new JLabel("Password:"));
        txtPassword = new JPasswordField();
        panel.add(txtPassword);

        btnLogin = new JButton("Login");
        panel.add(new JLabel()); 
        panel.add(btnLogin);

        add(new JLabel("  Toll Management System", SwingConstants.CENTER), BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);

        
        btnLogin.addActionListener(e -> {
            String username = txtUsername.getText().trim();
            String password = new String(txtPassword.getPassword()).trim();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter username and password.");
                return;
            }

            User user = authController.login(username, password);

            if (user == null) {
                JOptionPane.showMessageDialog(this, "Invalid credentials. Try again.");
                return;
            }

            dispose(); 

            
            switch (user.getRole()) {
                case "admin"      -> new AdminView(user).setVisible(true);
                case "operator"   -> new OperatorView(user).setVisible(true);
                case "supervisor" -> new SupervisorView(user).setVisible(true);
                case "owner"      -> new OwnerView(user).setVisible(true);
                default -> JOptionPane.showMessageDialog(null, "Unknown role.");
            }
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        new LoginView();
    }
}