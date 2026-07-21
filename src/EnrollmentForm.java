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
import java.time.LocalDate;


public class EnrollmentForm extends JFrame {


    JComboBox<String> cmbStudent;
    JComboBox<String> cmbCourse;

    JTextField txtSemester;
    JTextField txtYear;


    JButton btnEnroll;
    JButton btnDelete;
    JButton btnClear;


    JTable table;
    DefaultTableModel model;



    public EnrollmentForm(){

        setTitle("Enrollment Management");

        setSize(800,500);

        setLocationRelativeTo(null);

        setLayout(null);



        JLabel l1 =
        new JLabel("Student");

        JLabel l2 =
        new JLabel("Course");

        JLabel l3 =
        new JLabel("Semester");

        JLabel l4 =
        new JLabel("School Year");



        cmbStudent =
        new JComboBox<>();

        cmbCourse =
        new JComboBox<>();

        txtSemester =
        new JTextField();

        txtYear =
        new JTextField();



        l1.setBounds(20,20,100,25);
        cmbStudent.setBounds(130,20,200,25);


        l2.setBounds(20,60,100,25);
        cmbCourse.setBounds(130,60,200,25);


        l3.setBounds(20,100,100,25);
        txtSemester.setBounds(130,100,200,25);


        l4.setBounds(20,140,100,25);
        txtYear.setBounds(130,140,200,25);



        btnEnroll =
        new JButton("Enroll");


        btnDelete =
        new JButton("Delete");


        btnClear =
        new JButton("Clear");



        btnEnroll.setBounds(420,30,100,30);

        btnDelete.setBounds(420,70,100,30);

        btnClear.setBounds(420,110,100,30);



        add(l1);
        add(cmbStudent);

        add(l2);
        add(cmbCourse);

        add(l3);
        add(txtSemester);

        add(l4);
        add(txtYear);


        add(btnEnroll);
        add(btnDelete);
        add(btnClear);



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


        sp.setBounds(20,220,750,200);


        add(sp);



        loadStudents();

        loadCourses();

        loadEnrollments();



        btnEnroll.addActionListener(e->enrollStudent());

        btnDelete.addActionListener(e->deleteEnrollment());

        btnClear.addActionListener(e->clear());



        setVisible(true);

    }





    void loadStudents(){
        String sql = "SELECT * FROM students";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while(rs.next()){
                cmbStudent.addItem(
                    rs.getInt("student_id") + " - " + rs.getString("first_name") + " " + rs.getString("last_name")
                );
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    void loadCourses(){
        try{
            Connection con =
            DBConnection.getConnection();
            
            String sql = "SELECT * FROM courses";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
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

    public boolean checkDuplicateEnrollment(int studentId, int courseId) {

        boolean duplicate = false;

        try {

            Connection con = DBConnection.getConnection();

            String sql = "SELECT * FROM enrollments "
                       + "WHERE student_id = ? AND course_id = ?";

            PreparedStatement pst = con.prepareStatement(sql);

            pst.setInt(1, studentId);
            pst.setInt(2, courseId);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                duplicate = true;
            }

            rs.close();
            pst.close();
            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return duplicate;
    }



    void enrollStudent(){
        
        try{


            String student = cmbStudent.getSelectedItem().toString();
            String course = cmbCourse.getSelectedItem().toString();
            int studentId = Integer.parseInt(student.split("-")[0].trim());
            int courseId = Integer.parseInt(course.split("-")[0].trim());
            
            if (checkDuplicateEnrollment(studentId, courseId)) {
                JOptionPane.showMessageDialog(this, "Student is already enrolled!");
                return;
            }

            Connection con =
            DBConnection.getConnection();



            PreparedStatement ps = con.prepareStatement(
            "INSERT INTO enrollments(student_id, course_id, enrollment_date, semester, school_year, status) "
            + "VALUES(?, ?, ?, ?, ?, ?)"
            );



            ps.setInt(1,studentId);
            ps.setInt(2,courseId);
            ps.setDate(3,Date.valueOf(LocalDate.now()));
            ps.setString(4,txtSemester.getText());
            ps.setString(5,txtYear.getText());
            ps.setString(6, "Pending");
            ps.executeUpdate();



            JOptionPane.showMessageDialog(this, "Enrollment request submitted.\nWaiting for Admin Approval.");
            loadEnrollments();
        }catch(Exception e){

            e.printStackTrace();

        }

    }

    void loadEnrollments(){
        model.setRowCount(0);

        try{

            Connection con = DBConnection.getConnection();

            ResultSet rs = con.createStatement().executeQuery(
                "SELECT " +
                "enrollments.enrollment_id, " +
                "CONCAT(students.first_name, ' ', students.last_name), " +
                "courses.course_name, " +
                "enrollments.enrollment_date, " +
                "enrollments.semester, " +
                "enrollments.school_year, " +
                "enrollments.status " +
                "FROM enrollments " +
                "INNER JOIN students ON enrollments.student_id = students.student_id " +
                "INNER JOIN courses ON enrollments.course_id = courses.course_id"
            );
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

    void deleteEnrollment(){
        int row =
        table.getSelectedRow();
        if(row==-1){
            return;
        }
        int id = Integer.parseInt(model.getValueAt(row,0).toString());
        try{
            Connection con = DBConnection.getConnection();
            PreparedStatement pst = con.prepareStatement("DELETE FROM enrollments WHERE enrollment_id=?");

            pst.setInt(1,id);
            pst.executeUpdate();
            loadEnrollments();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    void clear(){
        txtSemester.setText("");
        txtYear.setText("");
    }
    public static void main(String[] args){
        new EnrollmentForm();
    }
}