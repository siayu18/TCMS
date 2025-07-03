package org.tcms.service;

import org.tcms.model.*;
import org.tcms.utils.FileHandler;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class UserService {
    protected final FileHandler accountFile;

    public UserService() throws IOException {
        accountFile = new FileHandler("account.csv", List.of("AccountID","Name","Password","Role"));
    }

    public List<User> getAllUsers() {
        return accountFile.readAll().stream()
                .map(row -> switch (row.get("Role")) {
                    case "Admin" -> new Admin(row.get("AccountID"), row.get("Name"), row.get("Password"), "Admin");
                    case "Student" -> new Student(row.get("AccountID"), row.get("Name"), row.get("Password"), "Student");
                    case "Tutor" -> new Tutor(row.get("AccountID"), row.get("Name"), row.get("Password"), "Tutor");
                    case "Receptionist" -> new Receptionist(row.get("AccountID"), row.get("Name"), row.get("Password"), "Receptionist");
                    default -> null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
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

    public void addUser(User user) {
        accountFile.append(Map.of(
                "AccountID", user.getAccountId(),
                "Name", user.getUsername(),
                "Password", user.getPassword(),
                "Role", user.getRole()
        ));
    }

    public void updateUser (String accountID, String newUsername, String newPassword) {
        List<Map<String, String>> rows = accountFile.readAll();

        for (Map<String, String> row : rows) {
            if (accountID.equals(row.get("AccountID"))) {
                row.put("Name", newUsername);
                row.put("Password", newPassword);
                break;
            }
        }
        accountFile.overwriteAll(rows);
    }

    public void deleteUser(String accountID) {
        List<Map<String, String>> rows = accountFile.readAll();
        rows.removeIf(row -> accountID.equals(row.get("AccountID")));
        accountFile.overwriteAll(rows);
    }
}
