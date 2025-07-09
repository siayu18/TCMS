package org.tcms.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class Tutor extends User {
    private String assignedLevel;
    private String assignedSubject;

    public Tutor(String id, String name, String pwd, String role) {
        super(id, name, pwd, role);
    }

    public Tutor(String id, String name, String pwd, String role, String assignedLevel, String assignedSubject){
        super(id, name, pwd, role);
        this.assignedLevel = assignedLevel;
        this.assignedSubject = assignedSubject;
    }

    // This fixes a bug with ComboBoxes (ReassignTutorController)
    @Override
    public String toString() {
        return getAccountId() + " - " + getUsername();
    }
}
