package org.tcms.utils;

import org.tcms.model.StudentPayment;
import org.tcms.model.Payment;
import org.tcms.model.Student;
import org.tcms.model.TuitionClass;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MappingUtils {
    public static List<StudentPayment> mapPaymentsForStudent(Student student, List<Payment> payments, Map<String, TuitionClass> classMap) {
        String studentID = student.getAccountId();

        return payments.stream()
                .filter(payment -> studentID.equals(payment.getStudentID()))
                .map(payment -> {
                    TuitionClass tuitionClass = classMap.get(payment.getClassID());
                    String subjectName = (tuitionClass != null ? tuitionClass.getSubjectName() : "Unknown");
                    return new StudentPayment(
                            payment.getPaymentID(),
                            payment.getStudentID(),
                            student.getUsername(),
                            payment.getClassID(),
                            subjectName,
                            payment.getAmount()
                    );
                })
                .collect(Collectors.toList());
    }
}
