package org.tcms.service;

import org.tcms.model.*;
import org.tcms.utils.FileHandler;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class UserService {
    private final FileHandler csv;

    public UserService() throws IOException {
        // read the account file
        csv = new FileHandler("account.csv", List.of("AccountID","Name","Password","Role"));
    }

    public User authenticate(String name, String password) {
        List<Map<String,String>> rows = csv.readAll();

        // Loop through every row
        for (Map<String, String> row : rows) {
            String rowName = row.get("Name");
            String rowPassword = row.get("Password");

            //  Check if this is the right user
            if (rowName.equals(name) && rowPassword.equals(password)) {
                return checkRoleAndCreate(row);
            }
        }
        // return null if no user is found
        return null;
    }

    private User checkRoleAndCreate(Map<String, String> row) {
        String id = row.get("AccountID");
        String name = row.get("Name");
        String pwd  = row.get("Password");
        String role = row.get("Role");

        return switch (role) {
            case "Admin" -> new Admin(id, name, pwd, role);
            case "Student" -> new Student(id, name, pwd, role);
            case "Tutor" -> new Tutor(id, name, pwd, role);
            case "Receptionist" -> new Receptionist(id, name, pwd, role);
            default -> null;
        };
    }
}
