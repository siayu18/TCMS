package org.tcms.service;

import org.tcms.model.Student;
import org.tcms.utils.FileHandler;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StudentService extends UserService {
    private final FileHandler studentFile;

    public StudentService() throws IOException {
        super();
        studentFile = new FileHandler("student.csv", Arrays.asList("StudentID","ICNumber","Email","ContactNumber","Address","Level"));
    }

    public List<Student> getAllStudents() {
        return getAllUsers().stream()
                .filter(u -> u instanceof Student)
                .map(u -> (Student)u)
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
