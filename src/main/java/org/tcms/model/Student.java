package org.tcms.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class Student extends User {
    private String icNumber;
    private String email;
    private String contactNumber;
    private String address;
    private String level;

    public Student(String id, String name, String pwd, String role) { super(id, name, pwd, role); }

    public Student(String id, String name, String pwd, String role, String icNumber, String email, String contactNumber, String address, String level) {
        super(id, name, pwd, role);
        this.icNumber = icNumber;
        this.email = email;
        this.contactNumber = contactNumber;
        this.address = address;
        this.level = level;
    }

    @Override public String getDashboardFxml() { return "StudentDashboardView.fxml"; }
    @Override public String getDashboardTitle() { return "Student Dashboard"; }
}
