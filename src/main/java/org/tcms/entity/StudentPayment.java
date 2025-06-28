package org.tcms.entity;

import javafx.beans.property.*;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StudentPayment {
    private final String paymentID;
    private final String studentID;
    private final String studentName;
    private final String classID;
    private final String subjectName;
    private final String amount;
}
