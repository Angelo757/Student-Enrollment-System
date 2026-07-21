/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author angel
 */
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EnrollmentApprovalForm extends JFrame implements ActionListener 
{
    JTable table;
    DefaultTableModel model;
    
    JTextField txtSearch;
    JButton btnSearch;
    JComboBox<String> cmbStatus;

    JButton btnApprove;
    JButton btnReject;
    JButton btnDelete;
    JButton btnRefresh;
    
    public EnrollmentApprovalForm() {

        setTitle("Enrollment Approval");
        setSize(850,500);
        setLocationRelativeTo(null);
        setLayout(null);
        
        txtSearch = new JTextField();
        btnSearch = new JButton("Search");

        cmbStatus = new JComboBox<>();

        cmbStatus.addItem("All");
        cmbStatus.addItem("Pending");
        cmbStatus.addItem("Approved");
        cmbStatus.addItem("Rejected");

        txtSearch.setBounds(20,310,180,30);
        btnSearch.setBounds(210,310,100,30);
        cmbStatus.setBounds(330,310,120,30);

        add(txtSearch);
        add(btnSearch);
        add(cmbStatus);

        model = new DefaultTableModel();

        model.addColumn("ID");
        model.addColumn("Student");
        model.addColumn("Course");
        model.addColumn("Date");
        model.addColumn("Semester");
        model.addColumn("School Year");
        model.addColumn("Status");

        table = new JTable(model);

        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(20,20,790,280);

        add(sp);

        btnApprove = new JButton("Approve");
        btnReject = new JButton("Reject");
        btnDelete = new JButton("Delete");
        btnRefresh = new JButton("Refresh");

        btnApprove.setBounds(70,340,140,35);
        btnReject.setBounds(240,340,140,35);
        btnDelete.setBounds(410,340,140,35);
        btnRefresh.setBounds(580,340,140,35);

        add(btnApprove);
        add(btnReject);
        add(btnDelete);
        add(btnRefresh);

        btnApprove.addActionListener(this);
        btnReject.addActionListener(this);
        btnDelete.addActionListener(this);
        btnRefresh.addActionListener(this);
        btnSearch.addActionListener(this);
        

        loadEnrollments();

        setVisible(true);
    }
    
    void loadEnrollments(){

        model.setRowCount(0);

        try{

            Connection con = DBConnection.getConnection();

            String sql =
            "SELECT enrollments.enrollment_id," +
            "CONCAT(students.first_name,' ',students.last_name)," +
            "courses.course_name," +
            "enrollments.enrollment_date," +
            "enrollments.semester," +
            "enrollments.school_year," +
            "enrollments.status " +
            "FROM enrollments " +
            "INNER JOIN students ON enrollments.student_id=students.student_id " +
            "INNER JOIN courses ON enrollments.course_id=courses.course_id";

            ResultSet rs = con.createStatement().executeQuery(sql);

            while(rs.next()){

                model.addRow(new Object[]{

                    rs.getInt(1),
                    rs.getString(2),
                    rs.getString(3),
                    rs.getDate(4),
                    rs.getString(5),
                    rs.getString(6),
                    rs.getString(7)

                });

            }

        }catch(Exception e){

            e.printStackTrace();

        }

    }
    
    void updateStatus(String status){
        
        
        int row = table.getSelectedRow();
        
        String currentStatus =
        model.getValueAt(row,6).toString();

        if(currentStatus.equalsIgnoreCase(status)){

            JOptionPane.showMessageDialog(this,
                    "Enrollment is already " + status + ".");

            return;

        }

        if(row == -1){

            JOptionPane.showMessageDialog(this,
                    "Please select an enrollment.");

            return;

        }
        
        int confirm =
        JOptionPane.showConfirmDialog(
        this,
        "Change status to " + status + "?",
        "Confirm",
        JOptionPane.YES_NO_OPTION);

        if(confirm!=JOptionPane.YES_OPTION)
            return;

        int id = Integer.parseInt(
                model.getValueAt(row,0).toString());

        try{

            Connection con = DBConnection.getConnection();

            PreparedStatement pst = con.prepareStatement(
                    "UPDATE enrollments SET status=? WHERE enrollment_id=?");

            pst.setString(1,status);
            pst.setInt(2,id);

            pst.executeUpdate();

            JOptionPane.showMessageDialog(this,
                    "Enrollment " + status + "!");

            loadEnrollments();

        }catch(Exception e){

            e.printStackTrace();

        }

    }
    void deleteEnrollment(){

        int row = table.getSelectedRow();

        String status =
        model.getValueAt(row,6).toString();

        if(status.equalsIgnoreCase("Approved")){

            JOptionPane.showMessageDialog(this,
                    "Approved enrollments cannot be deleted.");

            return;

        }
        if(row == -1){

            JOptionPane.showMessageDialog(this,
                    "Please select an enrollment.");

            return;

        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete this enrollment?",
                "Confirm",
                JOptionPane.YES_NO_OPTION);

        if(confirm != JOptionPane.YES_OPTION){

            return;

        }

        int id = Integer.parseInt(
                model.getValueAt(row,0).toString());

        try{

            Connection con = DBConnection.getConnection();

            PreparedStatement pst = con.prepareStatement(
                    "DELETE FROM enrollments WHERE enrollment_id=?");

            pst.setInt(1,id);

            pst.executeUpdate();

            JOptionPane.showMessageDialog(this,
                    "Enrollment deleted.");

            txtSearch.setText("");
            cmbStatus.setSelectedIndex(0);
            loadEnrollments();  

        }catch(Exception e){

            e.printStackTrace();

        }

    }
    
    void searchEnrollment(){

        if(txtSearch.getText().trim().isEmpty()){
            loadEnrollments();
            return;
        }

        model.setRowCount(0);

        String sql =
            "SELECT enrollments.enrollment_id," +
            "CONCAT(students.first_name,' ',students.last_name)," +
            "courses.course_name," +
            "enrollments.enrollment_date," +
            "enrollments.semester," +
            "enrollments.school_year," +
            "enrollments.status " +
            "FROM enrollments " +
            "INNER JOIN students ON enrollments.student_id=students.student_id " +
            "INNER JOIN courses ON enrollments.course_id=courses.course_id " +
            "WHERE students.first_name LIKE ? " +
            "OR students.last_name LIKE ? " +
            "OR courses.course_name LIKE ?";

        try(
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ){

            String search = "%" + txtSearch.getText() + "%";

            ps.setString(1, search);
            ps.setString(2, search);
            ps.setString(3, search);

            ResultSet rs = ps.executeQuery();

            while(rs.next()){

                model.addRow(new Object[]{

                    rs.getInt(1),
                    rs.getString(2),
                    rs.getString(3),
                    rs.getDate(4),
                    rs.getString(5),
                    rs.getString(6),
                    rs.getString(7)

                });

            }

        }catch(Exception e){

            e.printStackTrace();

        }

    }
    
    void filterStatus(){

        String status = cmbStatus.getSelectedItem().toString();

        if(status.equals("All")){
            loadEnrollments();
            return;
        }

        model.setRowCount(0);

        try(
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(

                "SELECT enrollments.enrollment_id," +
                "CONCAT(students.first_name,' ',students.last_name)," +
                "courses.course_name," +
                "enrollments.enrollment_date," +
                "enrollments.semester," +
                "enrollments.school_year," +
                "enrollments.status " +
                "FROM enrollments " +
                "INNER JOIN students ON enrollments.student_id=students.student_id " +
                "INNER JOIN courses ON enrollments.course_id=courses.course_id " +
                "WHERE enrollments.status=?"

            )
        ){

            ps.setString(1,status);

            ResultSet rs = ps.executeQuery();

            while(rs.next()){

                model.addRow(new Object[]{

                    rs.getInt(1),
                    rs.getString(2),
                    rs.getString(3),
                    rs.getDate(4),
                    rs.getString(5),
                    rs.getString(6),
                    rs.getString(7)

                });

            }

        }catch(Exception e){

            e.printStackTrace();

        }

    }
    
    @Override
    public void actionPerformed(ActionEvent e) {

        if(e.getSource() == btnApprove){

            updateStatus("Approved");

        }

        if(e.getSource() == btnReject){

            updateStatus("Rejected");

        }

        if(e.getSource() == btnDelete){

            deleteEnrollment();

        }

        if(e.getSource() == btnRefresh){

            loadEnrollments();

        }
        if(e.getSource() == btnSearch){

            searchEnrollment();

        }
        if(e.getSource() == cmbStatus){

            filterStatus();

        }

    }
    
    public static void main(String[] args){

        new EnrollmentApprovalForm();

    }
}