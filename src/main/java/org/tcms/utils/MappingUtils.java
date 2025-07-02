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
                            p.getAmount()
                    );
                })
                .collect(Collectors.toList());
    }
}
