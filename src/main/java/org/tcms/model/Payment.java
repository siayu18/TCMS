package org.tcms.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Payment {
    private String paymentID;
    private String studentID;
    private String enrollmentID;
    private String amount;
    private String date;
    private String time;
    private String status;
}
