
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class Dashboard extends JFrame implements ActionListener{

    JButton btnStudents;
    JButton btnCourses;
    JButton btnSubjects;
    JButton btnEnrollment;
    JButton btnLogout;


    public Dashboard() {

        setTitle("Student Enrollment Management System");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);


        JLabel title = new JLabel(
            "Student Enrollment Management System"
        );

        title.setBounds(100, 20, 300, 30);


        btnStudents = new JButton("Student Management");
        btnCourses = new JButton("Course Management");
        btnSubjects = new JButton("Subject Management");
        btnEnrollment = new JButton("Enrollment");
        btnLogout = new JButton("Logout");


        btnStudents.setBounds(130, 70, 230, 40);
        btnCourses.setBounds(130, 120, 230, 40);
        btnSubjects.setBounds(130, 170, 230, 40);
        btnEnrollment.setBounds(130, 220, 230, 40);
        btnLogout.setBounds(130, 270, 230, 40);


        add(title);

        add(btnStudents);
        add(btnCourses);
        add(btnSubjects);
        add(btnEnrollment);
        add(btnLogout);
        
        btnStudents.addActionListener(this);
        btnCourses.addActionListener(this);
        btnSubjects.addActionListener(this);
        btnEnrollment.addActionListener(this);
        btnLogout.addActionListener(this);
        
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == btnStudents){
            new StudentForm();
        }
        if(e.getSource() == btnCourses){
            new CourseForm();
        }
        if(e.getSource() == btnSubjects){
            new SubjectForm();
        }
        if(e.getSource() == btnEnrollment){
            new EnrollmentForm();
        }
        if(e.getSource() == btnLogout){
            new LoginForm();
        }
    }

}