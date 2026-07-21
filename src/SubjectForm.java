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

public class SubjectForm extends JFrame implements ActionListener{


    JComboBox<String> cmbCourse;
    JTextField txtSearch;

    JButton btnSearch;
    JButton btnUpdate;
    JTextField txtCode;
    JTextField txtName;
    JTextField txtUnits;


    JButton btnAdd;
    JButton btnDelete;
    JButton btnClear;


    JTable table;
    DefaultTableModel model;



    public SubjectForm(){


        setTitle("Subject Management");

        setSize(750,500);

        setLocationRelativeTo(null);

        setLayout(null);
        
        txtSearch = new JTextField();
        btnSearch = new JButton("Search");

        txtSearch.setBounds(20,180,180,30);
        btnSearch.setBounds(210,180,100,30);

        add(txtSearch);
        add(btnSearch);



        JLabel l1=new JLabel("Course");

        JLabel l2=new JLabel("Subject Code");

        JLabel l3=new JLabel("Subject Name");

        JLabel l4=new JLabel("Units");



        cmbCourse=new JComboBox<>();

        txtCode=new JTextField();

        txtName=new JTextField();

        txtUnits=new JTextField();



        l1.setBounds(20,20,100,25);
        cmbCourse.setBounds(130,20,180,25);


        l2.setBounds(20,60,100,25);
        txtCode.setBounds(130,60,180,25);


        l3.setBounds(20,100,100,25);
        txtName.setBounds(130,100,180,25);


        l4.setBounds(20,140,100,25);
        txtUnits.setBounds(130,140,180,25);



        btnAdd=new JButton("Add");

        btnDelete=new JButton("Delete");
        btnUpdate=new JButton("Update");
        

        btnClear=new JButton("Clear");



        btnAdd.setBounds(400,20,100,30);
        btnUpdate.setBounds(400,60,100,30);
        btnDelete.setBounds(400,100,100,30);
        btnClear.setBounds(400,140,100,30);
        add(btnUpdate);

        add(l1);
        add(cmbCourse);

        add(l2);
        add(txtCode);
        add(l3);
        add(txtName);

        add(l4);
        add(txtUnits);
        add(btnAdd);
        add(btnDelete);
        add(btnClear);
        
        model=new DefaultTableModel();

        model.addColumn("ID");

        model.addColumn("Course");

        model.addColumn("Code");

        model.addColumn("Subject Name");

        model.addColumn("Units");

        table=new JTable(model);
        
        JScrollPane sp=
        new JScrollPane(table);


        sp.setBounds(20,220,700,200);

        add(sp);
        
        loadCourses();

        loadSubjects();



        btnAdd.addActionListener(this);
        btnUpdate.addActionListener(this);
        btnDelete.addActionListener(this);
        btnClear.addActionListener(this);
        btnSearch.addActionListener(this);



        setVisible(true);

    }

    private boolean validateSubject(){

        if(cmbCourse.getSelectedItem()==null){

            JOptionPane.showMessageDialog(this,
                    "Please select a course.");

            return false;
        }

        if(txtCode.getText().trim().isEmpty() ||
           txtName.getText().trim().isEmpty() ||
           txtUnits.getText().trim().isEmpty()){

            JOptionPane.showMessageDialog(this,
                    "Complete all fields.");

            return false;
        }

        try{

            Integer.parseInt(txtUnits.getText());

        }catch(Exception e){

            JOptionPane.showMessageDialog(this,
                    "Units must be numeric.");

            return false;

        }

        return true;

    }

    private boolean subjectExists(String code){

        try{
            
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT subject_id FROM subjects WHERE subject_code=?");
       
            
            ps.setString(1,code);
            ResultSet rs = ps.executeQuery();
            return rs.next();

        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    void loadCourses(){

        try{
            Connection con = DBConnection.getConnection();


            ResultSet rs=con.createStatement().executeQuery("SELECT * FROM courses");


            while(rs.next()){
                cmbCourse.addItem(
                rs.getInt("course_id")
                +" - "+
                rs.getString("course_name")
                );

            }

        }catch(Exception e){

            e.printStackTrace();

        }

    }

    void addSubject(){


        try{
            if(!validateSubject())
                return;

            if(subjectExists(txtCode.getText())){

                JOptionPane.showMessageDialog(this,
                        "Subject code already exists.");

                return;

            }

            if(cmbCourse.getSelectedItem()==null){

                JOptionPane.showMessageDialog(
                null,
                "Add a course first"
                );

                return;

            }

            String selected = cmbCourse.getSelectedItem().toString();

            int courseId=Integer.parseInt(selected.split("-")[0].trim());

            Connection con = DBConnection.getConnection();

            PreparedStatement pst=
            con.prepareStatement(
            "INSERT INTO subjects"+
            "(course_id,subject_code,subject_name,units)"+
            "VALUES(?,?,?,?)"
            );

            pst.setInt(1,courseId);

            pst.setString(2,txtCode.getText());
            pst.setString(3,txtName.getText());
            pst.setInt(4,Integer.parseInt(txtUnits.getText()));
            pst.executeUpdate();
            
            JOptionPane.showMessageDialog(
            null,
            "Subject Added"
            );

            loadSubjects();
            clear();



        }catch(Exception e){

            e.printStackTrace();

        }

    }


    void updateSubject(){

        int row = table.getSelectedRow();

        if(row==-1){

            JOptionPane.showMessageDialog(this,
                    "Select a subject.");

            return;

        }

        if(!validateSubject())
            return;

        String selected =
        cmbCourse.getSelectedItem().toString();

        int courseId = Integer.parseInt(selected.split("-")[0].trim());

        int subjectId = Integer.parseInt(model.getValueAt(row,0).toString());

        String sql = "UPDATE subjects SET course_id=?, subject_code=?, subject_name=?, units=? WHERE subject_id=?";

        try(
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ){

            ps.setInt(1,courseId);
            ps.setString(2,txtCode.getText());
            ps.setString(3,txtName.getText());
            ps.setInt(4,Integer.parseInt(txtUnits.getText()));
            ps.setInt(5,subjectId);

            ps.executeUpdate();

            JOptionPane.showMessageDialog(this,
                    "Subject updated successfully.");

            loadSubjects();
            clear();

        }catch(Exception e){

            e.printStackTrace();

        }

    }

    void searchSubject(){

        if(txtSearch.getText().trim().isEmpty()){

            loadSubjects();

            return;

        }

        model.setRowCount(0);

        String sql =
        "SELECT subjects.subject_id, courses.course_name, subjects.subject_code, subjects.subject_name, subjects.units " +
        "FROM subjects INNER JOIN courses ON subjects.course_id=courses.course_id " +
        "WHERE subjects.subject_name LIKE ? OR subjects.subject_code LIKE ?";

        try(
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ){

            String search =
            "%" + txtSearch.getText() + "%";

            ps.setString(1,search);
            ps.setString(2,search);

            ResultSet rs = ps.executeQuery();

            while(rs.next()){

                model.addRow(new Object[]{

                    rs.getInt(1),
                    rs.getString(2),
                    rs.getString(3),
                    rs.getString(4),
                    rs.getInt(5)

                });

            }

        }catch(Exception e){

            e.printStackTrace();

        }

    }

    void loadSubjects(){


        model.setRowCount(0);

        try{


            Connection con=
            DBConnection.getConnection();

            ResultSet rs=
            con.createStatement()
            .executeQuery(

            "SELECT subjects.subject_id,"+
            "courses.course_name,"+
            "subjects.subject_code,"+
            "subjects.subject_name,"+
            "subjects.units "+
            "FROM subjects "+
            "INNER JOIN courses "+
            "ON subjects.course_id=courses.course_id"

            );

            while(rs.next()){


                model.addRow(new Object[]{


                    rs.getInt(1),

                    rs.getString(2),

                    rs.getString(3),

                    rs.getString(4),

                    rs.getInt(5)


                });


            }

        }catch(Exception e){

            e.printStackTrace();

        }


    }

    void deleteSubject(){


        int row = table.getSelectedRow();

        if(row==-1){

            JOptionPane.showMessageDialog(this,
                    "Please select a subject.");

            return;

        }

        int confirm =
        JOptionPane.showConfirmDialog(
        this,
        "Delete this subject?",
        "Confirm",
        JOptionPane.YES_NO_OPTION);

        if(confirm!=JOptionPane.YES_OPTION)
            return;



        int id=
        Integer.parseInt(
        model.getValueAt(row,0).toString()
        );



        try{


            Connection con=
            DBConnection.getConnection();



            PreparedStatement pst=
            con.prepareStatement(
            "DELETE FROM subjects WHERE subject_id=?"
            );


            pst.setInt(1,id);


            pst.executeUpdate();


            loadSubjects();



        }catch(Exception e){

            e.printStackTrace();

        }


    }

    void clear(){

        txtCode.setText("");
        txtName.setText("");
        txtUnits.setText("");

        table.clearSelection();

        cmbCourse.setSelectedIndex(0);

        txtCode.requestFocus();

    }

    public static void main(String[] args){

        new SubjectForm();

    }

    @Override
    public void actionPerformed(ActionEvent e) 
    {
        if(e.getSource() == btnAdd){
            addSubject();
        }
        if(e.getSource() == btnUpdate){
            updateSubject();
        }
        if(e.getSource() == btnDelete){
            deleteSubject();
        }
        if(e.getSource() == btnClear){
            clear();
        }
        if(e.getSource() == btnSearch){
            searchSubject();
        }
    }

}
