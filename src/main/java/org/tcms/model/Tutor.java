package org.tcms.model;

public class Tutor extends User {
    public Tutor(String id, String name, String pwd, String role) {
        super(id, name, pwd, role);
    }

    @Override public String getDashboardFxml() { return "TutorDashboardView.fxml"; }
    @Override public String getDashboardTitle() { return "Tutor Dashboard"; }
}
