package org.tcms.model;

public class Admin extends User {
    public Admin(String id, String name, String pwd, String role) {
        super(id, name, pwd, "Admin");
    }
}
