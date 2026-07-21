
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class RegisterForm extends JFrame implements ActionListener {

    JLabel lblTitle, lblFirstName, lblLastName, lblUsername, lblPassword;
    JTextField txtFirstName, txtLastName, txtUsername;
    JPasswordField txtPassword;
    JButton btnRegister, btnBack;

    public RegisterForm() {

        setTitle("Student Registration");
        setSize(500, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(null);

        lblTitle = new JLabel("STUDENT REGISTRATION");
        lblTitle.setBounds(110, 30, 300, 30);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));

        lblFirstName = new JLabel("First Name");
        lblFirstName.setBounds(60, 90, 100, 20);

        txtFirstName = new JTextField();
        txtFirstName.setBounds(60, 115, 350, 35);

        lblLastName = new JLabel("Last Name");
        lblLastName.setBounds(60, 160, 100, 20);

        txtLastName = new JTextField();
        txtLastName.setBounds(60, 185, 350, 35);

        lblUsername = new JLabel("Username");
        lblUsername.setBounds(60, 230, 100, 20);

        txtUsername = new JTextField();
        txtUsername.setBounds(60, 255, 350, 35);

        lblPassword = new JLabel("Password");
        lblPassword.setBounds(60, 300, 100, 20);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(60, 325, 350, 35);

        btnRegister = new JButton("REGISTER");
        btnRegister.setBounds(60, 390, 160, 40);
        btnRegister.addActionListener(this);

        btnBack = new JButton("BACK");
        btnBack.setBounds(250, 390, 160, 40);
        btnBack.addActionListener(this);

        add(lblTitle);
        add(lblFirstName);
        add(txtFirstName);
        add(lblLastName);
        add(txtLastName);
        add(lblUsername);
        add(txtUsername);
        add(lblPassword);
        add(txtPassword);
        add(btnRegister);
        add(btnBack);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == btnRegister) {

            String firstName = txtFirstName.getText().trim();
            String lastName = txtLastName.getText().trim();
            String username = txtUsername.getText().trim();
            String password = String.valueOf(txtPassword.getPassword());

            if (firstName.isEmpty() ||
                lastName.isEmpty() ||
                username.isEmpty() ||
                password.isEmpty()) {

                JOptionPane.showMessageDialog(this,
                        "Please complete all fields.");
                return;
            }

            try {

                Connection con = DBConnection.getConnection();

                
                String checkSql = "SELECT * FROM users WHERE Username=?";

                PreparedStatement check = con.prepareStatement(checkSql);
                check.setString(1, username);

                ResultSet rs = check.executeQuery();

                if (rs.next()) {

                    JOptionPane.showMessageDialog(this,
                            "Username already exists.");

                    return;

                }

                String sql = "INSERT INTO users(FirstName, LastName, Username, Password, Role) VALUES(?,?,?,?,?)";

                PreparedStatement ps = con.prepareStatement(sql);

                ps.setString(1, firstName);
                ps.setString(2, lastName);
                ps.setString(3, username);
                ps.setString(4, password);
                ps.setString(5, "Student");

                ps.executeUpdate();

                JOptionPane.showMessageDialog(this,
                        "Registration Successful!");

                new LoginForm();

                dispose();

                con.close();

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(this,
                        ex.getMessage());

            }

        }

        if (e.getSource() == btnBack) {

            new LoginForm();

            dispose();

        }

    }

    public static void main(String[] args) {

        new RegisterForm();

    }

}