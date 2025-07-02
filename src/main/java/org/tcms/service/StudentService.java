package org.tcms.service;

import org.tcms.model.Student;
import org.tcms.utils.FileHandler;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class StudentService extends UserService {
    private final FileHandler studentFile;

    public StudentService() throws IOException {
        super();
        studentFile = new FileHandler("student.csv", Arrays.asList("StudentID","ICNumber","Email","ContactNumber","Address","Level"));
    }

    public List<Student> getAllStudents() {
        Map<String, Map<String,String>> acctById = accountFile.readAll().stream()
                .collect(Collectors.toMap(
                        row -> row.get("AccountID"),
                        row -> row
                ));

        // for each student-detail row, look up its account and build a Student
        return studentFile.readAll().stream()
                .map(detail -> {
                    String id = detail.get("StudentID");
                    Map<String,String> acct = acctById.get(id);

                    if (acct == null) return null;

                    return new Student(
                            id,
                            acct.get("Name"),
                            acct.get("Password"),
                            acct.get("Role"),
                            detail.get("ICNumber"),
                            detail.get("Email"),
                            detail.get("ContactNumber"),
                            detail.get("Address"),
                            detail.get("Level")
                    );
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public void addStudent(Student student) {
        accountFile.append(Map.of(
                "AccountID", student.getAccountId(),
                "Name", student.getUsername(),
                "Password", student.getPassword(),
                "Role", student.getRole()
        ));
        studentFile.append(Map.of(
                "StudentID", student.getAccountId(),
                "ICNumber", student.getIcNumber(),
                "Email", student.getEmail(),
                "ContactNumber", student.getContactNumber(),
                "Address", student.getAddress(),
                "Level", student.getLevel()
        ));
    }
}
