package org.tcms.service;

import org.tcms.model.Student;
import org.tcms.utils.FileHandler;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ReceiptService {
    private final FileHandler receiptFile;

    public ReceiptService() throws IOException {
        receiptFile = new FileHandler("receipt.csv", Arrays.asList("ReceiptID","StudentID")
        );
    }

    public boolean isReceiptGenerated(Student student) {
        Map<String,String> rows = receiptFile.readAll().stream()
                .filter(row -> student.getAccountId().equalsIgnoreCase(row.get("StudentID")))
                .findFirst()
                .orElse(null);

        return rows != null;
    }

    public void addReceipt(Student student) {
        receiptFile.append(Map.of(
                "ReceiptID", UUID.randomUUID().toString(),
                "StudentID", student.getAccountId()
        ));
    }

    public void deleteReceipt(String studentID) {
        List<Map<String, String>> rows = receiptFile.readAll();
        rows.removeIf(row -> studentID.equals(row.get("StudentID")));
        receiptFile.overwriteAll(rows);
    }
}
