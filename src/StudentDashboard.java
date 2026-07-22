

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class StudentDashboard extends JFrame implements ActionListener {

    JLabel lblTitle, lblWelcome;

    JButton btnEnrollment;
    JButton btnMyEnrollment;
    JButton btnLogout;

    public StudentDashboard() {

        setTitle("Student Dashboard");
        setSize(700,450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(null);

        lblTitle = new JLabel("STUDENT DASHBOARD");
        lblTitle.setBounds(30,20,300,30);
        lblTitle.setFont(new Font("Segoe UI",Font.BOLD,22));

        lblWelcome = new JLabel("Welcome, " + Session.FirstName);
        lblWelcome.setBounds(500,25,180,20);

        btnEnrollment = new JButton("Enrollment Form");
        btnEnrollment.setBounds(200,90,250,40);
        btnEnrollment.addActionListener(this);

        btnMyEnrollment = new JButton("My Enrollment");
        btnMyEnrollment.setBounds(200,150,250,40);
        btnMyEnrollment.addActionListener(this);

        btnLogout = new JButton("Logout");
        btnLogout.setBounds(200,210,250,40);
        btnLogout.addActionListener(this);

        add(lblTitle);
        add(lblWelcome);
        add(btnEnrollment);
        add(btnMyEnrollment);
        add(btnLogout);

        setVisible(true);

    }
    
  

    @Override
    public void actionPerformed(ActionEvent e) {

        
        if(e.getSource()==btnEnrollment){

            new EnrollmentForm();
            dispose();

        }

        if(e.getSource()==btnMyEnrollment){

            
            new MyEnrollmentForm();
           
            dispose();

        }

        if(e.getSource()==btnLogout){

            new LoginForm();
            dispose();

        }

    }

}