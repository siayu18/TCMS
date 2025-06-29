package org.tcms.service;

import org.tcms.model.*;
import org.tcms.utils.FileHandler;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class UserService {
    private final FileHandler accountFile;

    public UserService() throws IOException {
        accountFile = new FileHandler("account.csv", List.of("AccountID","Name","Password","Role"));
    }

    public User authenticate(String name, String password) {
        return accountFile.readAll().stream()
                .map(row -> switch (row.get("Role")) {
                    case "Admin" -> new Admin(row.get("AccountID"), row.get("Name"), row.get("Password"), "Admin");
                    case "Student" -> new Student(row.get("AccountID"), row.get("Name"), row.get("Password"), "Student");
                    case "Tutor" -> new Tutor(row.get("AccountID"), row.get("Name"), row.get("Password"), "Tutor");
                    case "Receptionist" -> new Receptionist(row.get("AccountID"), row.get("Name"), row.get("Password"), "Receptionist");
                    default -> null;
                })
                .filter(Objects::nonNull)
                .filter(u -> u.getUsername().equals(name) && u.getPassword().equals(password))
                .findFirst()
                .orElse(null);
    }
}
