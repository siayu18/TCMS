package org.tcms.utils;

import org.tcms.model.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MappingUtils {
    public static List<StudentPayment> mapPaymentsForStudent(Student student, List<Payment> payments, Map<String, Enrollment> enrollmentMap, Map<String, TuitionClass> classMap) {
        String sid = student.getAccountId();
        return payments.stream()
                .filter(p -> sid.equals(p.getStudentID()))
                .map(p -> {
                    Enrollment rec = enrollmentMap.get(p.getEnrollmentID());
                    String classID = rec != null ? rec.getClassID() : null;
                    TuitionClass tc = classID != null ? classMap.get(classID) : null;
                    String subject = tc != null ? tc.getSubjectName() : "Unknown";
                    return new StudentPayment(
                            p.getPaymentID(),
                            p.getStudentID(),
                            student.getUsername(),
                            classID,
                            subject,
                            p.getAmount(),
                            p.getDate(),
                            p.getTime()
                    );
                })
                .collect(Collectors.toList());
    }

    public static List<Student> mapStudentForClasses(TuitionClass tuitionClass, Map<String, Student> student, List<Enrollment> enrollment) {
        String cid = tuitionClass.getClassID();
        return enrollment.stream()
                .filter(e -> cid.equals(e.getClassID()))
                .map(e -> {
                    Student stu = student.get(e.getStudentID());
                    return new Student(
                            stu.getAccountId(),
                            stu.getUsername(),
                            stu.getPassword(),
                            stu.getRole(),
                            stu.getIcNumber(),
                            stu.getEmail(),
                            stu.getContactNumber(),
                            stu.getAddress(),
                            stu.getLevel()
                    );
                })
                .collect(Collectors.toList());
    }
}
