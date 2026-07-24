/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class UserManagementForm extends JFrame implements ActionListener {

    JTable table;
    DefaultTableModel model;
    JLabel lblSearch;      // Added search label
    JTextField txtSearch;
    JButton btnSearch;
    JButton btnPromote;
    JButton btnDemote;
    JButton btnDelete;
    JButton btnRefresh;

    public UserManagementForm() {

        setTitle("User Management");
        setSize(700, 480);
        setLocationRelativeTo(null);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // --- Search Bar Section (Placed Top Left Above Table) ---
        lblSearch = new JLabel("Search:");
        txtSearch = new JTextField();
        btnSearch = new JButton("Search");

        lblSearch.setBounds(20, 20, 60, 25);
        txtSearch.setBounds(80, 20, 180, 25);
        btnSearch.setBounds(270, 20, 90, 25);

        add(lblSearch);
        add(txtSearch);
        add(btnSearch);

        // --- Table Setup ---
        model = new DefaultTableModel();
        model.addColumn("User ID");
        model.addColumn("Username");
        model.addColumn("Role");

        table = new JTable(model);

        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(20, 60, 640, 250); // Shifted down slightly to make room for search bar
        add(sp);

        // --- Action Buttons Section (Placed Below Table) ---
        btnPromote = new JButton("Make Admin");
        btnDemote = new JButton("Make Student");
        btnDelete = new JButton("Delete User");
        btnRefresh = new JButton("Refresh");

        btnPromote.setBounds(20, 330, 140, 35);
        btnDemote.setBounds(180, 330, 140, 35);
        btnDelete.setBounds(340, 330, 140, 35);
        btnRefresh.setBounds(500, 330, 140, 35);

        add(btnPromote);
        add(btnDemote);
        add(btnDelete);
        add(btnRefresh);

        // --- Listeners ---
        btnPromote.addActionListener(this);
        btnDemote.addActionListener(this);
        btnDelete.addActionListener(this);
        btnRefresh.addActionListener(this);
        btnSearch.addActionListener(this);

        // Auto-fill search field when a table row is clicked
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    txtSearch.setText(model.getValueAt(row, 1) != null ? model.getValueAt(row, 1).toString() : "");
                }
            }
        });

        loadUsers();
        setVisible(true);
    }

    void loadUsers() {
        model.setRowCount(0);

        try {
            Connection con = DBConnection.getConnection();
            ResultSet rs = con.createStatement().executeQuery("SELECT * FROM users");

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("user_id"),
                    rs.getString("username"),
                    rs.getString("role")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void searchUsers() {
        if (txtSearch.getText().trim().isEmpty()) {
            loadUsers();
            return;
        }

        model.setRowCount(0);

        String sql = "SELECT * FROM users WHERE username LIKE ? OR role LIKE ?";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {
            String search = "%" + txtSearch.getText().trim() + "%";

            ps.setString(1, search);
            ps.setString(2, search);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("user_id"),
                    rs.getString("username"),
                    rs.getString("role")
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void updateRole(String role) {
        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user.");
            return;
        }

        int id = Integer.parseInt(model.getValueAt(row, 0).toString());

        if (id == Session.UserID) {
            JOptionPane.showMessageDialog(this, "You cannot change your own role.");
            return;
        }

        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement pst = con.prepareStatement("UPDATE users SET role=? WHERE user_id=?");

            pst.setString(1, role);
            pst.setInt(2, id);

            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "Role updated successfully.");

            loadUsers();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void deleteUser() {
        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to permanently delete this user?",
            "Delete User",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        int id = Integer.parseInt(model.getValueAt(row, 0).toString());

        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement pst = con.prepareStatement("DELETE FROM users WHERE user_id=?");

            pst.setInt(1, id);
            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "User deleted.");

            loadUsers();
            txtSearch.setText("");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new UserManagementForm();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnPromote) {
            updateRole("admin");
        } else if (e.getSource() == btnDemote) {
            updateRole("student");
        } else if (e.getSource() == btnDelete) {
            deleteUser();
        } else if (e.getSource() == btnRefresh) {
            txtSearch.setText("");
            loadUsers();
        } else if (e.getSource() == btnSearch) {
            searchUsers();
        }
    }
}