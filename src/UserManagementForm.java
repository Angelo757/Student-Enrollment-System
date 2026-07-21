/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author angel
 */
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class UserManagementForm extends JFrame implements ActionListener{

    JTable table;
    DefaultTableModel model;
    JTextField txtSearch;
    JButton btnSearch;
    JButton btnPromote;
    JButton btnDemote;
    JButton btnDelete;
    JButton btnRefresh;

    public UserManagementForm(){

        setTitle("User Management");
        setSize(700,450);
        setLocationRelativeTo(null);
        setLayout(null);
        
        txtSearch = new JTextField();
        btnSearch = new JButton("Search");

        txtSearch.setBounds(20,280,180,30);
        btnSearch.setBounds(210,280,100,30);

        add(txtSearch);
        add(btnSearch);

        model = new DefaultTableModel();

        model.addColumn("User ID");
        model.addColumn("Username");
        model.addColumn("Role");

        table = new JTable(model);

        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(20,20,640,250);

        add(sp);

        btnPromote = new JButton("Make Admin");
        btnDemote = new JButton("Make Student");
        btnDelete = new JButton("Delete User");
        btnRefresh = new JButton("Refresh");

        btnPromote.setBounds(20,300,140,35);
        btnDemote.setBounds(180,300,140,35);
        btnDelete.setBounds(340,300,140,35);
        btnRefresh.setBounds(500,300,140,35);

        add(btnPromote);
        add(btnDemote);
        add(btnDelete);
        add(btnRefresh);

        btnPromote.addActionListener(this);
        btnDemote.addActionListener(this);
        btnDelete.addActionListener(this);
        btnRefresh.addActionListener(this);
        btnSearch.addActionListener(this);

        loadUsers();

        setVisible(true);
    }

    void loadUsers(){

        model.setRowCount(0);

        try{

            Connection con = DBConnection.getConnection();

            ResultSet rs = con.createStatement().executeQuery(
                    "SELECT * FROM users");

            while(rs.next()){

                model.addRow(new Object[]{

                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("role")
                        
                });
            }
        }catch(Exception e){

            e.printStackTrace();
        }
    }

    void searchUsers(){

        if(txtSearch.getText().trim().isEmpty()){
            loadUsers();
            return;
        }

        model.setRowCount(0);

        String sql =
            "SELECT * FROM users WHERE username LIKE ? OR role LIKE ?";

        try(
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ){

            String search = "%" + txtSearch.getText() + "%";

            ps.setString(1, search);
            ps.setString(2, search);

            ResultSet rs = ps.executeQuery();

            while(rs.next()){

                model.addRow(new Object[]{

                    rs.getInt("user_id"),
                    rs.getString("username"),
                    rs.getString("role")

                });

            }

        }catch(Exception e){

            e.printStackTrace();

        }

    }
    void updateRole(String role){

        int row = table.getSelectedRow();
        
        int id = Integer.parseInt(
        model.getValueAt(row,0).toString());

        if(id == Session.UserID){

            JOptionPane.showMessageDialog(this,
                    "You cannot change your own role.");

            return;

        }

        if(row==-1){

            JOptionPane.showMessageDialog(this,
                    "Please select a user.");

            return;
        }
        try{

            Connection con = DBConnection.getConnection();

            PreparedStatement pst = con.prepareStatement(
                    "UPDATE users SET role=? WHERE user_id=?");

            pst.setString(1,role);
            pst.setInt(2,id);

            pst.executeUpdate();

            JOptionPane.showMessageDialog(this,
                    "Role updated successfully.");

            loadUsers();
        }catch(Exception e){

            e.printStackTrace();
        }
    }
    void deleteUser(){

        int row = table.getSelectedRow();
        
        
        if(row == -1){

            JOptionPane.showMessageDialog(this,
                    "Please select a user.");
            return;
        }

        int confirm =
        JOptionPane.showConfirmDialog(
        this,
        "Are you sure you want to permanently delete this user?",
        "Delete User",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.WARNING_MESSAGE);

        if(confirm != JOptionPane.YES_OPTION)
            return;

        int id = Integer.parseInt(
                model.getValueAt(row,0).toString());

        try{

            Connection con = DBConnection.getConnection();

            PreparedStatement pst = con.prepareStatement(
                    "DELETE FROM users WHERE user_id=?");

            pst.setInt(1,id);

            pst.executeUpdate();

            JOptionPane.showMessageDialog(this,
                    "User deleted.");

            loadUsers();
            txtSearch.setText("");

        }catch(Exception e){

            e.printStackTrace();
        }
    }


    public static void main(String[] args){
        new UserManagementForm();
    }

    @Override
    public void actionPerformed(ActionEvent e) 
    {
        if(e.getSource() == btnPromote){
            updateRole("admin");
        }
        if(e.getSource() == btnDemote){
            updateRole("student");
        }
        if(e.getSource() == btnDelete){
            deleteUser();
        }
        if(e.getSource() == btnRefresh){
            loadUsers();
        }
        if(e.getSource() == btnSearch){
            searchUsers();
        }
    }
}
