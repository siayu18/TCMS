package org.tcms.service;

import org.tcms.model.Student;
import org.tcms.utils.FileHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class StudentService {
    private FileHandler accountFile;

    public StudentService() {
        accountFile = new FileHandler("account.csv", Arrays.asList("AccountID", "Name", "Password", "Role"));
    }

    public List<Student> getAllStudents() {
        List<Map<String, String>> rows = accountFile.readAll();
        List<Student> students = new ArrayList<>();
        for (Map<String, String> row : rows) {
            if ("Student".equalsIgnoreCase(row.get("Role"))) {
                students.add(new Student(
                        row.get("AccountID"),
                        row.get("Name"),
                        row.get("Password"),
                        row.get("Role")
                ));
            }
        }
        return students;
    }

    public void updateStudent (String accountID, String newUsername, String newPassword) {
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

    public void deleteStudent (String accountID) {
        List<Map<String, String>> rows = accountFile.readAll();
        rows.removeIf(row -> accountID.equals(row.get("AccountID")));
        accountFile.overwriteAll(rows);
    }

}
