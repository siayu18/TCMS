package org.tcms.service;

import org.tcms.model.Enrollment;
import org.tcms.utils.FileHandler;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class EnrollmentService {
    private final FileHandler enrollmentFile;

    public EnrollmentService() throws IOException {
        enrollmentFile = new FileHandler("enrollment.csv", Arrays.asList("EnrollmentID","StudentID","EnrollmentDate","ClassID"));
    }

    public List<Enrollment> getAllEnrollment() {
        return enrollmentFile.readAll().stream()
                .map(row -> new Enrollment(
                        row.get("EnrollmentID"),
                        row.get("StudentID"),
                        LocalDate.parse(row.get("EnrollmentDate")),
                        row.get("ClassID")
                ))
                .collect(Collectors.toList());
    }

    public void addEnrollment(Enrollment record) {
        enrollmentFile.append(Map.of(
                "EnrollmentID", record.getEnrollmentID(),
                "StudentID", record.getStudentID(),
                "EnrollmentDate", record.getEnrollmentDate().toString(),
                "ClassID", record.getClassID()
        ));
    }

    public List<Enrollment> getByStudent(String studentId) {
        return getAllEnrollment().stream()
                .filter(record -> record.getStudentID().equals(studentId))
                .collect(Collectors.toList());
    }

    public void deleteEnrollment(String enrollmentID) {
        var rows = enrollmentFile.readAll();
        rows.removeIf(r -> r.get("EnrollmentID").equals(enrollmentID));
        enrollmentFile.overwriteAll(rows);
    }

    public void deleteEnrollmentFromStudent(String studentID) {
        var rows = enrollmentFile.readAll();
        rows.removeIf(r -> r.get("StudentID").equals(studentID));
        enrollmentFile.overwriteAll(rows);
    }

    public Enrollment getEnrollmentByID(String enrollmentID) {
        return getAllEnrollment().stream() // Assume getAllEnrollment() returns all enrollments
                .filter(enroll -> enroll.getEnrollmentID().equals(enrollmentID))
                .findFirst()
                .orElse(null); // Return null if not found
    }

    public String transferEnrollment(String studentID, String oldClassID, String newClassID) {
        Enrollment enrollment = getAllEnrollment().stream()
                .filter(enroll -> oldClassID.equals(enroll.getClassID()) && studentID.equals(enroll.getStudentID()))
                .findFirst()
                .orElse(null);

        String oldEnrollmentID = null;

        if (enrollment != null) {
            oldEnrollmentID = enrollment.getEnrollmentID();
            deleteEnrollment(enrollment.getEnrollmentID());
        } else {
            System.out.println("Warning: No matching enrollment found for student " + studentID + " in class " + oldClassID);
        }

        addEnrollment(new Enrollment(
                UUID.randomUUID().toString(),
                studentID,
                LocalDate.now(),
                newClassID
        ));

        return oldEnrollmentID;
    }
}
