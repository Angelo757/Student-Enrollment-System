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

public class CourseForm extends JFrame implements ActionListener{


    JTextField txtCode;
    JTextField txtName;
    JTextField txtDescription;
    JTextField txtUnits;
    JTextField txtSearch;
    JButton btnSearch;


    JButton btnAdd;
    JButton btnDelete;
    JButton btnClear;
    JButton btnUpdate;
    


    JTable table;
    DefaultTableModel model;



    public CourseForm(){


        setTitle("Course Management");

        setSize(700,450);

        setLocationRelativeTo(null);

        setLayout(null);
        txtSearch = new JTextField();
        btnSearch = new JButton("Search");

        txtSearch.setBounds(20,180,180,30);
        btnSearch.setBounds(210,180,100,30);
        
       

        add(txtSearch);
        add(btnSearch);



        JLabel l1=new JLabel("Course Code");
        JLabel l2=new JLabel("Course Name");
        JLabel l3=new JLabel("Description");
        JLabel l4=new JLabel("Units");



        txtCode=new JTextField();
        txtName=new JTextField();
        txtDescription=new JTextField();
        txtUnits=new JTextField();



        l1.setBounds(20,20,100,25);
        txtCode.setBounds(130,20,150,25);


        l2.setBounds(20,60,100,25);
        txtName.setBounds(130,60,150,25);


        l3.setBounds(20,100,100,25);
        txtDescription.setBounds(130,100,150,25);


        l4.setBounds(20,140,100,25);
        txtUnits.setBounds(130,140,150,25);



        btnAdd=new JButton("Add");
        btnDelete=new JButton("Delete");
        btnClear=new JButton("Clear");
        btnUpdate = new JButton("Update");

        // Dito natin inayos ang pwesto ng Add button at iba pang buttons
        btnAdd.setBounds(350,30,100,30);
        btnUpdate.setBounds(350,70,100,30);
        btnDelete.setBounds(350,110,100,30);
        btnClear.setBounds(350,150,100,30);



        add(l1);
        add(txtCode);

        add(l2);
        add(txtName);

        add(l3);
        add(txtDescription);

        add(l4);
        add(txtUnits);


        add(btnAdd);
        add(btnUpdate);
        add(btnDelete);
        add(btnClear);



        model=new DefaultTableModel();


        model.addColumn("ID");
        model.addColumn("Code");
        model.addColumn("Course Name");
        model.addColumn("Description");
        model.addColumn("Units");



        table=new JTable(model);


        JScrollPane sp=
        new JScrollPane(table);


        sp.setBounds(20,220,630,150);


        add(sp);



        btnAdd.addActionListener(this);
        btnUpdate.addActionListener(this);
        btnDelete.addActionListener(this);
        btnClear.addActionListener(this);
        btnSearch.addActionListener(this);   



        loadCourses();



        setVisible(true);

    }

    private boolean courseExists(String code){

        try(
            Connection con = DBConnection.getConnection();
            PreparedStatement ps =
                con.prepareStatement(
                "SELECT course_id FROM courses WHERE course_code=?")
        ){

            ps.setString(1,code);

            ResultSet rs = ps.executeQuery();

            return rs.next();

        }catch(Exception e){

            e.printStackTrace();

        }

        return false;
    }
    private boolean validateCourse(){

        if(txtCode.getText().trim().isEmpty() ||
           txtName.getText().trim().isEmpty() ||
           txtDescription.getText().trim().isEmpty() ||
           txtUnits.getText().trim().isEmpty()){

            JOptionPane.showMessageDialog(this,
                    "Complete all fields.");
            return false;
        }

        try{
            Integer.parseInt(txtUnits.getText());
        }catch(Exception e){
            JOptionPane.showMessageDialog(this,
                    "Units must be a number.");
            return false;
        }

        return true;
    }

    void addCourse(){


        try{
            if(!validateCourse())
                return;

            if(courseExists(txtCode.getText())){
                JOptionPane.showMessageDialog(this,
                        "Course code already exists.");
                return;
            }

            Connection con=
            DBConnection.getConnection();



            String sql=
            "INSERT INTO courses"+
            "(course_code,course_name,description,units)"+
            "VALUES(?,?,?,?)";



            PreparedStatement pst=
            con.prepareStatement(sql);



            pst.setString(1,txtCode.getText());

            pst.setString(2,txtName.getText());

            pst.setString(3,txtDescription.getText());

            pst.setInt(4,
            Integer.parseInt(txtUnits.getText()));



            pst.executeUpdate();



            JOptionPane.showMessageDialog(
            null,
            "Course Added"
            );


            loadCourses();
            clear();



        }catch(Exception e){

            e.printStackTrace();

        }

    }


    void updateCourse(){

        int row = table.getSelectedRow();

        if(row==-1){

            JOptionPane.showMessageDialog(this,
                    "Select a course.");

            return;
        }

        if(!validateCourse())
            return;

        int id = Integer.parseInt(
                model.getValueAt(row,0).toString());

        String sql =
        "UPDATE courses SET course_code=?,course_name=?,description=?,units=? WHERE course_id=?";

        try(
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ){

            ps.setString(1,txtCode.getText());
            ps.setString(2,txtName.getText());
            ps.setString(3,txtDescription.getText());
            ps.setInt(4,Integer.parseInt(txtUnits.getText()));
            ps.setInt(5,id);

            ps.executeUpdate();

            JOptionPane.showMessageDialog(this,
                    "Course updated.");

            loadCourses();
            clear();

        }catch(Exception e){

            e.printStackTrace();

        }

    }
    void searchCourse(){

        if(txtSearch.getText().trim().isEmpty()){
            loadCourses();
            return;
        }

        model.setRowCount(0);

        try(
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(
            "SELECT * FROM courses WHERE course_name LIKE ? OR course_code LIKE ?")
        ){

            String search = "%" + txtSearch.getText() + "%";

            ps.setString(1,search);
            ps.setString(2,search);

            ResultSet rs = ps.executeQuery();

            while(rs.next()){

                model.addRow(new Object[]{
                    rs.getInt("course_id"),
                    rs.getString("course_code"),
                    rs.getString("course_name"),
                    rs.getString("description"),
                    rs.getInt("units")
                });

            }

        }catch(Exception e){

            e.printStackTrace();

        }

    }

    void loadCourses(){
        
        model.setRowCount(0);

        try{


            Connection con = DBConnection.getConnection();

            ResultSet rs = con.createStatement().executeQuery("SELECT * FROM courses");
            while(rs.next()){
                model.addRow(new Object[]{

                    rs.getInt("course_id"),

                    rs.getString("course_code"),

                    rs.getString("course_name"),

                    rs.getString("description"),

                    rs.getInt("units")

                });
            }
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    void deleteCourse(){

        int row = table.getSelectedRow();

        if(row == -1){

            JOptionPane.showMessageDialog(this,"Select a course.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete this course?",
                "Confirm",
                JOptionPane.YES_NO_OPTION);

        if(confirm!=JOptionPane.YES_OPTION)
            return;
        
        int id = Integer.parseInt(model.getValueAt(row,0).toString());

        try{
            Connection con = DBConnection.getConnection();
            PreparedStatement pst=
            con.prepareStatement(
            "DELETE FROM courses WHERE course_id=?"
            );
            pst.setInt(1,id);
            pst.executeUpdate();
            loadCourses();
            clear();
        }catch(Exception e){

            e.printStackTrace();

        }


    }

    void clear(){

        txtCode.setText("");
        txtName.setText("");
        txtDescription.setText("");
        txtUnits.setText("");

        table.clearSelection();

        txtCode.requestFocus();

    }

    public static void main(String args[]){

        new CourseForm();

    }

     
    @Override
    public void actionPerformed(ActionEvent e) 
    {
        if(e.getSource() == btnAdd){
            addCourse();
        }
        if(e.getSource() == btnUpdate){
            updateCourse();
        }
        if(e.getSource() == btnDelete){
            deleteCourse();
        }
        if(e.getSource() == btnClear){
            clear();
        }
        if(e.getSource() == btnSearch){
            searchCourse();
        }
    }

}