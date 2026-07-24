import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class StudentForm extends JFrame implements ActionListener {

    JTextField txtUserId;
    JTextField txtFirst;
    JTextField txtLast;
    JTextField txtCourseId;
    JTextField txtYearLevel;
    
    JTextField txtSearch;
    JButton btnSearch;

    JButton btnAdd;
    JButton btnUpdate;
    JButton btnDelete;
    JButton btnClear;

    JTable table;
    DefaultTableModel model;

    public StudentForm() {

        setTitle("Student Management System");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Search Panel
        txtSearch = new JTextField();
        btnSearch = new JButton("Search");

        txtSearch.setBounds(20, 150, 200, 25);
        btnSearch.setBounds(230, 150, 100, 25);
        btnSearch.addActionListener(this);

        add(txtSearch);
        add(btnSearch);

        // Labels
        JLabel l1 = new JLabel("User ID");
        JLabel l2 = new JLabel("First Name");
        JLabel l3 = new JLabel("Last Name");
        JLabel l4 = new JLabel("Course ID");
        JLabel l5 = new JLabel("Year Level");

        // TextFields
        txtUserId = new JTextField();
        txtFirst = new JTextField();
        txtLast = new JTextField();
        txtCourseId = new JTextField();
        txtYearLevel = new JTextField();

        l1.setBounds(20, 20, 100, 25);
        txtUserId.setBounds(120, 20, 150, 25);

        l2.setBounds(20, 55, 100, 25);
        txtFirst.setBounds(120, 55, 150, 25);

        l3.setBounds(20, 90, 100, 25);
        txtLast.setBounds(120, 90, 150, 25);

        l4.setBounds(300, 20, 100, 25);
        txtCourseId.setBounds(400, 20, 150, 25);

        l5.setBounds(300, 55, 100, 25);
        txtYearLevel.setBounds(400, 55, 150, 25);

        // Buttons
        btnAdd = new JButton("Add");
        btnUpdate = new JButton("Update");
        btnDelete = new JButton("Delete");
        btnClear = new JButton("Clear");

        btnAdd.setBounds(600, 20, 100, 30);
        btnUpdate.setBounds(600, 55, 100, 30);
        btnDelete.setBounds(600, 90, 100, 30);
        btnClear.setBounds(600, 125, 100, 30);

        add(l1); add(txtUserId);
        add(l2); add(txtFirst);
        add(l3); add(txtLast);
        add(l4); add(txtCourseId);
        add(l5); add(txtYearLevel);

        add(btnAdd);
        add(btnUpdate);
        add(btnDelete);
        add(btnClear);

        // Table setup with exact database columns
        model = new DefaultTableModel();

        model.addColumn("Student ID");
        model.addColumn("User ID");
        model.addColumn("First Name");
        model.addColumn("Last Name");
        model.addColumn("Course ID");
        model.addColumn("Year Level");

        table = new JTable(model);
        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(20, 185, 740, 250);
        add(sp);

        // Register Action Listeners
        btnAdd.addActionListener(this);
        btnUpdate.addActionListener(this);
        btnDelete.addActionListener(this);
        btnClear.addActionListener(this);

        // Fixed Mouse Click Listener mapping table columns to form inputs
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    txtUserId.setText(model.getValueAt(row, 1) != null ? model.getValueAt(row, 1).toString() : "");
                    txtFirst.setText(model.getValueAt(row, 2) != null ? model.getValueAt(row, 2).toString() : "");
                    txtLast.setText(model.getValueAt(row, 3) != null ? model.getValueAt(row, 3).toString() : "");
                    txtCourseId.setText(model.getValueAt(row, 4) != null ? model.getValueAt(row, 4).toString() : "");
                    txtYearLevel.setText(model.getValueAt(row, 5) != null ? model.getValueAt(row, 5).toString() : "");
                }
            }
        });

        loadStudents();
        setVisible(true);
    }

    private boolean validateInput() {
        if (txtFirst.getText().trim().isEmpty() ||
            txtLast.getText().trim().isEmpty() ||
            txtCourseId.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(this, "First Name, Last Name, and Course ID are required.");
            return false;
        }
        return true;
    }

    void loadStudents() {
        model.setRowCount(0);

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM students")) {

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("student_id"),
                    rs.getObject("user_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getObject("course_id"),
                    rs.getObject("year_level")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void searchStudent() {
        model.setRowCount(0);

        if (txtSearch.getText().trim().isEmpty()) {
            loadStudents();
            return;
        }

        String sql = "SELECT * FROM students WHERE first_name LIKE ? OR last_name LIKE ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            String search = "%" + txtSearch.getText().trim() + "%";
            pst.setString(1, search);
            pst.setString(2, search);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("student_id"),
                    rs.getObject("user_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getObject("course_id"),
                    rs.getObject("year_level")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void addStudent() {
        if (!validateInput()) return;

        String sql = "INSERT INTO students (user_id, first_name, last_name, course_id, year_level) VALUES (?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            if (txtUserId.getText().trim().isEmpty()) {
                pst.setNull(1, Types.INTEGER);
            } else {
                pst.setInt(1, Integer.parseInt(txtUserId.getText().trim()));
            }

            pst.setString(2, txtFirst.getText().trim());
            pst.setString(3, txtLast.getText().trim());
            pst.setInt(4, Integer.parseInt(txtCourseId.getText().trim()));

            if (txtYearLevel.getText().trim().isEmpty()) {
                pst.setNull(5, Types.INTEGER);
            } else {
                pst.setInt(5, Integer.parseInt(txtYearLevel.getText().trim()));
            }

            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Student Added Successfully");

            loadStudents();
            clear();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding student: " + e.getMessage());
        }
    }

    void updateStudent() {
        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a student from table first.");
            return;
        }

        if (!validateInput()) return;

        int studentId = Integer.parseInt(model.getValueAt(row, 0).toString());
        String sql = "UPDATE students SET user_id=?, first_name=?, last_name=?, course_id=?, year_level=? WHERE student_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            if (txtUserId.getText().trim().isEmpty()) {
                pst.setNull(1, Types.INTEGER);
            } else {
                pst.setInt(1, Integer.parseInt(txtUserId.getText().trim()));
            }

            pst.setString(2, txtFirst.getText().trim());
            pst.setString(3, txtLast.getText().trim());
            pst.setInt(4, Integer.parseInt(txtCourseId.getText().trim()));

            if (txtYearLevel.getText().trim().isEmpty()) {
                pst.setNull(5, Types.INTEGER);
            } else {
                pst.setInt(5, Integer.parseInt(txtYearLevel.getText().trim()));
            }

            pst.setInt(6, studentId);

            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Student Updated Successfully");

            loadStudents();
            clear();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating student: " + e.getMessage());
        }
    }

    void deleteStudent() {
        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a student to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Delete this student?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        int id = Integer.parseInt(model.getValueAt(row, 0).toString());

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement("DELETE FROM students WHERE student_id=?")) {

            pst.setInt(1, id);
            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "Student Deleted Successfully");

            loadStudents();
            clear();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void clear() {
        txtUserId.setText("");
        txtFirst.setText("");
        txtLast.setText("");
        txtCourseId.setText("");
        txtYearLevel.setText("");

        table.clearSelection();
        txtUserId.requestFocus();
    }

    public static void main(String[] args) {
        new StudentForm();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSearch) {
            searchStudent();
        } else if (e.getSource() == btnAdd) {
            addStudent();
        } else if (e.getSource() == btnUpdate) {
            updateStudent();
        } else if (e.getSource() == btnDelete) {
            deleteStudent();
        } else if (e.getSource() == btnClear) {
            clear();
        }
    }
}