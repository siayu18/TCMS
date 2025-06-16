package org.tcms.model;

public class Student extends User {
    public Student(String id, String name, String pwd, String role) {
        super(id, name, pwd, role);
    }

    @Override public String getDashboardFxml() { return "StudentDashboardView.fxml"; }
    @Override public String getDashboardTitle() { return "Student Dashboard"; }
}
