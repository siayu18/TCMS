package org.tcms.model;

public class Receptionist extends User {
    public Receptionist(String id, String name, String pwd, String role) {
        super(id, name, pwd, role);
    }

    @Override public String getDashboardFxml() { return "ReceptionistDashboardView.fxml"; }
    @Override public String getDashboardTitle() { return "Receptionist Dashboard"; }
}
