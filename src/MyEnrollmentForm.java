import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class MyEnrollmentForm extends JFrame implements ActionListener {

    JLabel lblTitle;
    JLabel lblStudentID, lblName, lblCourse, lblYear, lblStatus;

    JLabel txtStudentID;
    JLabel txtName;
    JLabel txtCourse;
    JLabel txtYear;
    JLabel txtStatus;

    JButton btnRefresh;
    JButton btnClose;

    public MyEnrollmentForm() {

        setTitle("My Enrollment");
        setSize(550, 420);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);

        lblTitle = new JLabel("MY ENROLLMENT");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setBounds(150, 20, 250, 40);
        add(lblTitle);

        lblStudentID = new JLabel("Student ID:");
        lblStudentID.setBounds(60, 90, 100, 25);
        add(lblStudentID);

        txtStudentID = new JLabel("-");
        txtStudentID.setBounds(180, 90, 250, 25);
        add(txtStudentID);

        lblName = new JLabel("Full Name:");
        lblName.setBounds(60, 130, 100, 25);
        add(lblName);

        txtName = new JLabel("-");
        txtName.setBounds(180, 130, 250, 25);
        add(txtName);

        lblCourse = new JLabel("Course:");
        lblCourse.setBounds(60, 170, 100, 25);
        add(lblCourse);

        txtCourse = new JLabel("-");
        txtCourse.setBounds(180, 170, 250, 25);
        add(txtCourse);

        lblYear = new JLabel("School Year:");
        lblYear.setBounds(60, 210, 100, 25);
        add(lblYear);

        txtYear = new JLabel("-");
        txtYear.setBounds(180, 210, 250, 25);
        add(txtYear);

        lblStatus = new JLabel("Status:");
        lblStatus.setBounds(60, 250, 100, 25);
        add(lblStatus);

        txtStatus = new JLabel("-");
        txtStatus.setBounds(180, 250, 250, 25);
        txtStatus.setFont(new Font("Segoe UI", Font.BOLD, 16));
        txtStatus.setForeground(Color.BLUE);
        add(txtStatus);

        btnRefresh = new JButton("Refresh");
        btnRefresh.setBounds(120, 320, 120, 35);
        btnRefresh.addActionListener(this);
        add(btnRefresh);

        btnClose = new JButton("Close");
        btnClose.setBounds(280, 320, 120, 35);
        btnClose.addActionListener(this);
        add(btnClose);

        loadEnrollment();

        setVisible(true);
    }

    private void loadEnrollment() {
        try {
            int currentStudentId = StudentData.StudentID;

            if (currentStudentId == 0) {
                txtStatus.setText("No Student ID found");
                return;
            }

            Connection con = DBConnection.getConnection();

            
            String sql = "SELECT students.student_id, " +
                         "CONCAT(students.first_name, ' ', students.last_name) AS Name, " +
                         "courses.course_name, " +
                         "enrollments.school_year, " +
                         "enrollments.status " +
                         "FROM enrollments, students, courses " +
                         "WHERE enrollments.student_id = students.student_id " +
                         "AND enrollments.course_id = courses.course_id " +
                         "AND students.student_id = ?";

            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, currentStudentId);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                txtStudentID.setText(rs.getString("student_id"));
                txtName.setText(rs.getString("Name"));
                txtCourse.setText(rs.getString("course_name"));
                txtYear.setText(rs.getString("school_year"));
                txtStatus.setText(rs.getString("status"));
            } else {
                txtStudentID.setText(String.valueOf(currentStudentId));
                txtName.setText(StudentData.FirstName + " " + StudentData.LastName);
                txtCourse.setText("-");
                txtYear.setText("-");
                txtStatus.setText("Not Yet Enrolled");
            }

            rs.close();
            pst.close();
            con.close();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnRefresh) {
            loadEnrollment();
        }

        if (e.getSource() == btnClose) {
            dispose();
        }
    }
}