package org.tcms.model;

public class Admin extends User {
    public Admin(String id, String name, String pwd) {
        super(id, name, pwd, "Admin");
    }

    @Override
    public String getDashboardFxml() {
        return "AdminDashboardView.fxml";
    }


    @Override
    public String getDashboardTitle() {
        return "Admin Dashboard";
    }
}
