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

public class AdminDashboard extends JFrame implements ActionListener{

    JButton btnStudents;
    JButton btnCourses;
    JButton btnSubjects;
    JButton btnEnrollment;
    JButton btnUsers;
    JButton btnLogout;

    public AdminDashboard(){

        setTitle("Admin Dashboard");
        setSize(550,450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        JLabel title = new JLabel("STUDENT ENROLLMENT MANAGEMENT SYSTEM");
        title.setBounds(90,20,350,30);

        JLabel lblAdmin = new JLabel("ADMIN DASHBOARD");
        lblAdmin.setBounds(180,50,200,30);

        btnStudents = new JButton("Student Management");
        btnCourses = new JButton("Course Management");
        btnSubjects = new JButton("Subject Management");
        btnEnrollment = new JButton("Enrollment Management");
        btnUsers = new JButton("User Management");
        btnLogout = new JButton("Logout");

        btnStudents.setBounds(140,100,250,35);
        btnCourses.setBounds(140,145,250,35);
        btnSubjects.setBounds(140,190,250,35);
        btnEnrollment.setBounds(140,235,250,35);
        btnUsers.setBounds(140,280,250,35);
        btnLogout.setBounds(140,325,250,35);

        add(title);
        add(lblAdmin);

        add(btnStudents);
        add(btnCourses);
        add(btnSubjects);
        add(btnEnrollment);
        add(btnUsers);
        add(btnLogout);

        btnStudents.addActionListener(this);
        btnCourses.addActionListener(this);
        btnSubjects.addActionListener(this);
        btnEnrollment.addActionListener(this);
        btnUsers.addActionListener(this);
        btnLogout.addActionListener(this);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if(e.getSource()==btnStudents){
            new StudentForm();
        }
        if(e.getSource()==btnCourses){
            new CourseForm();
        }
        if(e.getSource()==btnSubjects){
            new SubjectForm();
        }
        if(e.getSource()==btnEnrollment){
            new EnrollmentApprovalForm();
        }
        if(e.getSource()==btnUsers){
            new UserManagementForm();
        }
        if(e.getSource()==btnLogout){

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to logout?",
                    "Logout",
                    JOptionPane.YES_NO_OPTION);

            if(confirm==JOptionPane.YES_OPTION){
                new LoginForm();
                dispose();
            }
        }
    }
    public static void main(String[] args) {
        new AdminDashboard();
    }
}