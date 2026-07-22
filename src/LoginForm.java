

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginForm extends JFrame implements ActionListener {

    // Components
    JLabel lblTitle, lblUsername, lblPassword, lblRegister;
    JTextField txtUsername;
    JPasswordField txtPassword;
    JButton btnLogin, btnRegister;

    public LoginForm() {

        // Frame
        setTitle("Student Enrollment Management System");
        setSize(850, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(null);

        // Left Panel
        JPanel leftPanel = new JPanel();
        leftPanel.setBounds(0, 0, 300, 500);
        leftPanel.setBackground(new Color(44, 62, 80));
        leftPanel.setLayout(null);

        JLabel lblWelcome = new JLabel("Welcome!");
        lblWelcome.setBounds(60, 80, 200, 40);
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblWelcome.setForeground(Color.WHITE);

        JLabel lblSystem = new JLabel("<html>Student Enrollment<br>Management System</html>");
        lblSystem.setBounds(40, 150, 250, 60);
        lblSystem.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblSystem.setForeground(Color.WHITE);

        JLabel lblDesc = new JLabel("<html>Manage student enrollment<br>quickly and easily.</html>");
        lblDesc.setBounds(40, 240, 220, 40);
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblDesc.setForeground(Color.LIGHT_GRAY);

        leftPanel.add(lblWelcome);
        leftPanel.add(lblSystem);
        leftPanel.add(lblDesc);

        add(leftPanel);

        // Right Panel
        JPanel rightPanel = new JPanel();
        rightPanel.setBounds(300, 0, 550, 500);
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setLayout(null);

        lblTitle = new JLabel("LOGIN");
        lblTitle.setBounds(180, 40, 200, 40);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));

        lblUsername = new JLabel("Username");
        lblUsername.setBounds(100, 120, 100, 20);

        txtUsername = new JTextField();
        txtUsername.setBounds(100, 145, 300, 35);

        lblPassword = new JLabel("Password");
        lblPassword.setBounds(100, 200, 100, 20);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(100, 225, 300, 35);

        btnLogin = new JButton("LOGIN");
        btnLogin.setBounds(100, 290, 300, 40);
        btnLogin.setBackground(new Color(52, 152, 219));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.addActionListener(this);

        lblRegister = new JLabel("Don't have an account?");
        lblRegister.setBounds(140, 360, 150, 20);

        btnRegister = new JButton("Register");
        btnRegister.setBounds(250, 355, 90, 30);
        btnRegister.setBorderPainted(false);
        btnRegister.setContentAreaFilled(false);
        btnRegister.setForeground(new Color(52, 152, 219));
        btnRegister.addActionListener(this);

        rightPanel.add(lblTitle);
        rightPanel.add(lblUsername);
        rightPanel.add(txtUsername);
        rightPanel.add(lblPassword);
        rightPanel.add(txtPassword);
        rightPanel.add(btnLogin);
        rightPanel.add(lblRegister);
        rightPanel.add(btnRegister);

        add(rightPanel);

        setVisible(true);
    }
    
    public void login() {
        String username = txtUsername.getText().trim();
        String password = String.valueOf(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter username and password.");
            return;
        }

        try {
            Connection con = DBConnection.getConnection();
            String sql = "SELECT * FROM users WHERE username=? AND password=?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, username);
            pst.setString(2, password);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");

                JOptionPane.showMessageDialog(this, "Login Successful!");
                dispose();

                if (role.equalsIgnoreCase("Admin")) {
                    new AdminDashboard();
                } else {
                    String fName = rs.getString("FirstName");
                    String lName = rs.getString("LastName");

                    rs.close();
                    pst.close();

                    
                    String studentSql = "SELECT student_id FROM students WHERE first_name = ? AND last_name = ?";
                    PreparedStatement studentPs = con.prepareStatement(studentSql);
                    studentPs.setString(1, fName);
                    studentPs.setString(2, lName);
                    ResultSet studentRs = studentPs.executeQuery();

                    if(studentRs.next()){
                        
                        StudentData.StudentID = studentRs.getInt("student_id");
                        StudentData.FirstName = fName;
                        StudentData.LastName = lName;
                    } else {
                        
                        StudentData.StudentID = 0; 
                    } 
                    
                    studentRs.close();
                    studentPs.close();
                    con.close();

                    new StudentDashboard(); 
                    dispose();
                }

            } else {
                JOptionPane.showMessageDialog(this, "Invalid Username or Password.");
            }

            rs.close();
            pst.close();
            con.close();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) 
    {
        if (e.getSource() == btnLogin) 
        {
            login();

        }
        if (e.getSource() == btnRegister) 
        {
            new RegisterForm();
            this.dispose();
        }
    }
}
    