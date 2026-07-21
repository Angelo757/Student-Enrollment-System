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

public class StudentForm extends JFrame implements ActionListener{

    JTextField txtFirst;
    JTextField txtLast;
    JTextField txtGender;
    JTextField txtEmail;
    JTextField txtPhone;
    JTextField txtAddress;
    
    JTextField txtSearch;
    JButton btnSearch;

    JButton btnAdd;
    JButton btnUpdate;
    JButton btnDelete;
    JButton btnClear;

    JTable table;
    DefaultTableModel model;


    public StudentForm(){

        setTitle("Student Management");
        setSize(800,500);
        setLocationRelativeTo(null);
        setLayout(null);
        
        txtSearch = new JTextField();

        btnSearch = new JButton("Search");
        


        txtSearch.setBounds(20,170,200,30);

        btnSearch.setBounds(230,170,100,30);
        btnSearch.addActionListener(this);


        add(txtSearch);
        add(btnSearch);


        JLabel l1=new JLabel("First Name");
        JLabel l2=new JLabel("Last Name");
        JLabel l3=new JLabel("Gender");
        JLabel l4=new JLabel("Email");
        JLabel l5=new JLabel("Phone");
        JLabel l6=new JLabel("Address");


        txtFirst=new JTextField();
        txtLast=new JTextField();
        txtGender=new JTextField();
        txtEmail=new JTextField();
        txtPhone=new JTextField();
        txtAddress=new JTextField();


        l1.setBounds(20,20,100,25);
        txtFirst.setBounds(120,20,150,25);

        l2.setBounds(20,55,100,25);
        txtLast.setBounds(120,55,150,25);

        l3.setBounds(20,90,100,25);
        txtGender.setBounds(120,90,150,25);

        l4.setBounds(300,20,100,25);
        txtEmail.setBounds(400,20,150,25);

        l5.setBounds(300,55,100,25);
        txtPhone.setBounds(400,55,150,25);

        l6.setBounds(300,90,100,25);
        txtAddress.setBounds(400,90,150,25);


        btnAdd=new JButton("Add");
        btnUpdate=new JButton("Update");
        btnDelete=new JButton("Delete");
        btnClear=new JButton("Clear");


        btnAdd.setBounds(600,20,100,30);
        btnUpdate.setBounds(600,55,100,30);
        btnDelete.setBounds(600,90,100,30);
        btnClear.setBounds(600,125,100,30);



        add(l1); add(txtFirst);
        add(l2); add(txtLast);
        add(l3); add(txtGender);
        add(l4); add(txtEmail);
        add(l5); add(txtPhone);
        add(l6); add(txtAddress);


        add(btnAdd);
        add(btnUpdate);
        add(btnDelete);
        add(btnClear);



        model=new DefaultTableModel();

        model.addColumn("ID");
        model.addColumn("First Name");
        model.addColumn("Last Name");
        model.addColumn("Gender");
        model.addColumn("Email");
        model.addColumn("Phone");
        model.addColumn("Address");


        table=new JTable(model);

        JScrollPane sp=new JScrollPane(table);

        sp.setBounds(20,180,740,200);

        add(sp);



        btnAdd.addActionListener(this);

        btnDelete.addActionListener(this);

        btnClear.addActionListener(this);
       

        table.addMouseListener(new java.awt.event.MouseAdapter(){

            public void mouseClicked(
            java.awt.event.MouseEvent e){

                int row=table.getSelectedRow();

                txtFirst.setText(
                model.getValueAt(row,1).toString());

                txtLast.setText(
                model.getValueAt(row,2).toString());

                txtGender.setText(
                model.getValueAt(row,3).toString());

                txtEmail.setText(
                model.getValueAt(row,4).toString());

                txtPhone.setText(
                model.getValueAt(row,5).toString());

                txtAddress.setText(
                model.getValueAt(row,6).toString());

            }

        });



        loadStudents();

        setVisible(true);

    }
    
    private boolean validateInput() {

        if(txtFirst.getText().trim().isEmpty() ||
           txtLast.getText().trim().isEmpty() ||
           txtGender.getText().trim().isEmpty() ||
           txtEmail.getText().trim().isEmpty() ||
           txtPhone.getText().trim().isEmpty() ||
           txtAddress.getText().trim().isEmpty()){

            JOptionPane.showMessageDialog(this,
                    "Please complete all fields.");

            return false;
        }

        if(!txtEmail.getText().matches(
                "^[A-Za-z0-9+_.-]+@(.+)$")){

            JOptionPane.showMessageDialog(this,
                    "Invalid email address.");

            return false;
        }

        return true;
    }
    
    private boolean emailExists(String email){

        String sql =
        "SELECT student_id FROM students WHERE email=?";

        try(
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ){

            ps.setString(1,email);

            ResultSet rs = ps.executeQuery();

            return rs.next();

        }catch(Exception ex){

            ex.printStackTrace();

        }

        return false;
    }
    
    void searchStudent(){
        
        
        model.setRowCount(0);


        try{
            if(txtSearch.getText().trim().isEmpty()){

                loadStudents();
                return;
            }
            Connection con =
            DBConnection.getConnection();


            String sql =
            "SELECT * FROM students WHERE first_name LIKE ? OR last_name LIKE ?";


            PreparedStatement pst =
            con.prepareStatement(sql);


            String search =
            "%" + txtSearch.getText() + "%";


            pst.setString(1,search);

            pst.setString(2,search);



            ResultSet rs =
            pst.executeQuery();



            while(rs.next()){


                model.addRow(new Object[]{

                    rs.getInt("student_id"),

                    rs.getString("first_name"),

                    rs.getString("last_name"),

                    rs.getString("gender"),

                    rs.getString("email"),

                    rs.getString("phone"),

                    rs.getString("address")

                });
            }  
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }



    void addStudent(){

        try{
            if(!validateInput())
            return;

        if(emailExists(txtEmail.getText())){
            JOptionPane.showMessageDialog(this,
                    "Email already exists.");
            return;
        }

            Connection con=DBConnection.getConnection();


            String sql=
            "INSERT INTO students"+
            "(first_name,last_name,gender,email,phone,address)"+
            "VALUES(?,?,?,?,?,?)";


            PreparedStatement pst=
            con.prepareStatement(sql);


            pst.setString(1,txtFirst.getText());
            pst.setString(2,txtLast.getText());
            pst.setString(3,txtGender.getText());
            pst.setString(4,txtEmail.getText());
            pst.setString(5,txtPhone.getText());
            pst.setString(6,txtAddress.getText());


            pst.executeUpdate();


            JOptionPane.showMessageDialog(
            null,"Student Added");


            loadStudents();
            clear();


        }catch(Exception e){

            e.printStackTrace();

        }

    }
    
    void updateStudent(){

        int row = table.getSelectedRow();

        if(row==-1){

            JOptionPane.showMessageDialog(this,
                    "Select a student first.");

            return;
        }

        if(!validateInput())
            return;

        int id = Integer.parseInt(
                model.getValueAt(row,0).toString());

        String sql =
        "UPDATE students SET first_name=?,last_name=?,gender=?,email=?,phone=?,address=? WHERE student_id=?";

        try(
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ){

            ps.setString(1,txtFirst.getText());
            ps.setString(2,txtLast.getText());
            ps.setString(3,txtGender.getText());
            ps.setString(4,txtEmail.getText());
            ps.setString(5,txtPhone.getText());
            ps.setString(6,txtAddress.getText());
            ps.setInt(7,id);

            ps.executeUpdate();

            JOptionPane.showMessageDialog(this,
                    "Student updated successfully.");

            loadStudents();
            clear();

        }catch(Exception ex){

            ex.printStackTrace();

        }

    }



    void loadStudents(){

        model.setRowCount(0);


        try{

            Connection con=DBConnection.getConnection();


            ResultSet rs=
            con.createStatement()
            .executeQuery(
            "SELECT * FROM students"
            );


            while(rs.next()){

                model.addRow(new Object[]{
                    rs.getInt("student_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("gender"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("address")
                });

            }


        }catch(Exception e){

            e.printStackTrace();

        }

    }



    void deleteStudent(){

        int row=table.getSelectedRow();

        

    if(row==-1){

        JOptionPane.showMessageDialog(this,
                "Select a student.");

        return;
    }

        int confirm =
        JOptionPane.showConfirmDialog(
        this,
        "Delete this student?",
        "Confirm",
        JOptionPane.YES_NO_OPTION);

        if(confirm!=JOptionPane.YES_OPTION)
            return;


        int id=(int)
        model.getValueAt(row,0);


        try{

            Connection con=
            DBConnection.getConnection();


            PreparedStatement pst=
            con.prepareStatement(
            "DELETE FROM students WHERE student_id=?"
            );


            pst.setInt(1,id);

            pst.executeUpdate();


            loadStudents();
            clear();


        }catch(Exception e){

            e.printStackTrace();

        }

    }



    void clear(){

        txtFirst.setText("");
        txtLast.setText("");
        txtGender.setText("");
        txtEmail.setText("");
        txtPhone.setText("");
        txtAddress.setText("");

        table.clearSelection();

        txtFirst.requestFocus();

    }


    public static void main(String[] args){

        new StudentForm();

    }
    

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == btnSearch){
            searchStudent();
        }
        if(e.getSource() == btnAdd){
            addStudent();
        }
        if(e.getSource() == btnDelete){
            deleteStudent();
        }
        if(e.getSource() == btnClear){
            clear();
        }
    }

}