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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.LocalDate;

public class EnrollmentForm extends JFrame implements ActionListener {

    JTextField txtFirstName;
    JTextField txtLastName;
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
        setSize(850, 520);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel l1 = new JLabel("First Name");
        JLabel l2 = new JLabel("Last Name");
        JLabel l3 = new JLabel("Course");
        JLabel l4 = new JLabel("Semester");
        JLabel l5 = new JLabel("School Year");

        txtFirstName = new JTextField();
        txtLastName = new JTextField();
        cmbCourse = new JComboBox<>();
        txtSemester = new JTextField();
        txtYear = new JTextField();

        l1.setBounds(20, 20, 100, 25);
        txtFirstName.setBounds(130, 20, 200, 25);

        l2.setBounds(20, 60, 100, 25);
        txtLastName.setBounds(130, 60, 200, 25);

        l3.setBounds(20, 100, 100, 25);
        cmbCourse.setBounds(130, 100, 200, 25);

        l4.setBounds(20, 140, 100, 25);
        txtSemester.setBounds(130, 140, 200, 25);

        l5.setBounds(20, 180, 100, 25);
        txtYear.setBounds(130, 180, 200, 25);

        btnEnroll = new JButton("Enroll");
        btnDelete = new JButton("Delete");
        btnClear = new JButton("Clear");

        btnEnroll.setBounds(420, 30, 100, 30);
        btnDelete.setBounds(420, 70, 100, 30);
        btnClear.setBounds(420, 110, 100, 30);

        add(l1);
        add(txtFirstName);
        add(l2);
        add(txtLastName);
        add(l3);
        add(cmbCourse);
        add(l4);
        add(txtSemester);
        add(l5);
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
        sp.setBounds(20, 230, 790, 220);
        add(sp);

        loadCourses();
        loadEnrollments();

        // Pag-attach ng ActionListeners sa mga button
        btnEnroll.addActionListener(this);
        btnDelete.addActionListener(this);
        btnClear.addActionListener(this);

        setVisible(true);
    }

    void loadCourses(){
        try{
            Connection con = DBConnection.getConnection();
            String sql = "SELECT * FROM courses";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()){
                cmbCourse.addItem(
                    rs.getInt("course_id") + " - " + rs.getString("course_name")
                );
            }
            rs.close();
            ps.close();
            con.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    void enrollStudent(){
        try{
            String firstName = txtFirstName.getText().trim();
            String lastName = txtLastName.getText().trim();
            String course = cmbCourse.getSelectedItem().toString();
            String semester = txtSemester.getText().trim();
            String year = txtYear.getText().trim();

            if(firstName.isEmpty() || lastName.isEmpty() || semester.isEmpty() || year.isEmpty()){
                JOptionPane.showMessageDialog(this, "Please complete all fields.");
                return;
            }

            int courseId = Integer.parseInt(course.split("-")[0].trim());

            Connection con = DBConnection.getConnection();

            // Step 1: I-save muna ang student sa 'students' table para magkaroon ng sariling student_id
            String studentSql = "INSERT INTO students(first_name, last_name, course_id) VALUES(?, ?, ?)";
            PreparedStatement studentPs = con.prepareStatement(studentSql, Statement.RETURN_GENERATED_KEYS);
            studentPs.setString(1, firstName);
            studentPs.setString(2, lastName);
            studentPs.setInt(3, courseId);
            studentPs.executeUpdate();

            // Kunin ang bagong generated student_id
            ResultSet rsKeys = studentPs.getGeneratedKeys();
            int studentId = 0;
            if (rsKeys.next()) {
                studentId = rsKeys.getInt(1);
            }
            studentPs.close();

            // Step 2: I-update ang 'users' table kung saan magkapangalan sila para hindi na maging NULL ang student_id
            String updateUserSql = "UPDATE users SET student_id = ? WHERE FirstName = ? AND LastName = ?";
            PreparedStatement updatePs = con.prepareStatement(updateUserSql);
            updatePs.setInt(1, studentId);
            updatePs.setString(2, firstName);
            updatePs.setString(3, lastName);
            updatePs.executeUpdate();
            updatePs.close();

            // Step 2: I-save na ngayon sa 'enrollments' table gamit ang nakuha nating studentId
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO enrollments(student_id, course_id, enrollment_date, semester, school_year, status) "
                + "VALUES(?, ?, ?, ?, ?, ?)"
            );

            ps.setInt(1, studentId);
            ps.setInt(2, courseId);
            ps.setDate(3, Date.valueOf(LocalDate.now()));
            ps.setString(4, semester);
            ps.setString(5, year);
            ps.setString(6, "Pending");
            ps.executeUpdate();
            ps.close();
            con.close();

            JOptionPane.showMessageDialog(this, "Student registered and enrollment request submitted!");
            loadEnrollments();
            clear();

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    void loadEnrollments(){
        model.setRowCount(0);

        try{
            Connection con = DBConnection.getConnection();

            // Paggamit ng comma at WHERE (walang JOIN) para i-load ang mga nakasulat sa table
            String sql = "SELECT enrollments.enrollment_id, " +
                         "CONCAT(students.first_name, ' ', students.last_name), " +
                         "courses.course_name, " +
                         "enrollments.enrollment_date, " +
                         "enrollments.semester, " +
                         "enrollments.school_year, " +
                         "enrollments.status " +
                         "FROM enrollments, students, courses " +
                         "WHERE enrollments.student_id = students.student_id " +
                         "AND enrollments.course_id = courses.course_id";

            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
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
            rs.close();
            stmt.close();
            con.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    void deleteEnrollment(){
        int row = table.getSelectedRow();
        if(row == -1){
            JOptionPane.showMessageDialog(this, "Please select an enrollment to delete.");
            return;
        }
        
        int id = Integer.parseInt(model.getValueAt(row, 0).toString());
        try{
            Connection con = DBConnection.getConnection();
            PreparedStatement pst = con.prepareStatement("DELETE FROM enrollments WHERE enrollment_id=?");
            pst.setInt(1, id);
            pst.executeUpdate();
            pst.close();
            con.close();
            
            loadEnrollments();
            JOptionPane.showMessageDialog(this, "Enrollment deleted successfully.");
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    void clear(){
        txtFirstName.setText("");
        txtLastName.setText("");
        txtSemester.setText("");
        txtYear.setText("");
        table.clearSelection();
        txtFirstName.requestFocus();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == btnEnroll){
            enrollStudent();
        }
        if(e.getSource() == btnDelete){
            deleteEnrollment();
        }
        if(e.getSource() == btnClear){
            clear();
        }
    }

    public static void main(String[] args){
        new EnrollmentForm();
    }
}