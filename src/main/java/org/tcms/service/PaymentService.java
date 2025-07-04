package org.tcms.service;

import org.tcms.model.Payment;
import org.tcms.utils.FileHandler;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PaymentService {
    private final FileHandler paymentFile;

    public PaymentService() throws IOException {
        paymentFile = new FileHandler("payment.csv", Arrays.asList("PaymentID","StudentID","EnrollmentID","Amount","Date","Time","Status")
        );
    }

    public List<Payment> getAllPayments() {
        return paymentFile.readAll().stream()
                .map(row -> new Payment(
                        row.get("PaymentID"),
                        row.get("StudentID"),
                        row.get("EnrollmentID"),
                        row.get("Amount"),
                        row.get("Date"),
                        row.get("Time"),
                        row.get("Status")
                ))
                .collect(Collectors.toList());
    }

    public List<Payment> getUnacceptedPayments() {
        return getAllPayments().stream()
                .filter(row -> "unaccepted".equalsIgnoreCase(row.getStatus()))
                .collect(Collectors.toList());
    }

    public void updatePaymentStatus(String paymentID) {
        List<Map<String,String>> rows = paymentFile.readAll();

        for (Map<String,String> row : rows) {
            if (paymentID.equals(row.get("PaymentID"))) {
                row.put("Status", "accepted");
                break;
            }
        }

        paymentFile.overwriteAll(rows);
    }
}
