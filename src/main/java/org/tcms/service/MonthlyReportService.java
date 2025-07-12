package org.tcms.service;

import org.tcms.model.MonthlyReportEntry;
import org.tcms.model.Payment;
import org.tcms.model.Enrollment;
import org.tcms.model.TuitionClass;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MonthlyReportService {
    private final PaymentService paymentService;
    private final EnrollmentService enrollmentService;
    private final TuitionClassService tuitionClassService;

    public MonthlyReportService(
            PaymentService paymentService,
            EnrollmentService enrollmentService,
            TuitionClassService tuitionClassService
    ) {
        this.paymentService = paymentService;
        this.enrollmentService = enrollmentService;
        this.tuitionClassService = tuitionClassService;
    }

    public List<MonthlyReportEntry> generateReport(
            String selectedMonth,
            String selectedSubject,
            String selectedLevel
    ) {
        List<Payment> acceptedPayments = paymentService.getAllPayments().stream()
                .filter(payment -> "accepted".equalsIgnoreCase(payment.getStatus()))
                .toList();

        Map<String, Map<String, Object>> aggregatedData = acceptedPayments.stream()
                .filter(payment -> matchesMonth(payment, selectedMonth))
                .collect(Collectors.groupingBy(
                        this::getSubjectLevelKey,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                payments -> {
                                    double totalIncome = payments.stream()
                                            .mapToDouble(p -> Double.parseDouble(p.getAmount()))
                                            .sum();
                                    return Map.of("totalIncome", totalIncome);
                                }
                        )
                ));

        return aggregatedData.entrySet().stream()
                .filter(entry -> matchesSubjectLevelFilter(entry.getKey(), selectedSubject, selectedLevel))
                .map(entry -> {
                    String[] subjectLevel = entry.getKey().split("\\|");
                    Map<String, Object> data = entry.getValue();
                    return new MonthlyReportEntry(
                            subjectLevel[0], // Subject
                            subjectLevel[1], // Level
                            (double) data.get("totalIncome")
                    );
                })
                .collect(Collectors.toList());
    }

    private String getSubjectLevelKey(Payment payment) {
        Enrollment enrollment = enrollmentService.getEnrollmentByID(payment.getEnrollmentID());
        if (enrollment == null) return "Unknown|Unknown";

        TuitionClass cls = tuitionClassService.getClassByID(enrollment.getClassID());
        if (cls == null) return "Unknown|Unknown";

        return cls.getSubjectName() + "|" + cls.getLevel();
    }

    private boolean matchesSubjectLevelFilter(String subjectLevelKey, String selectedSubject, String selectedLevel) {
        String[] parts = subjectLevelKey.split("\\|");
        String subject = parts[0];
        String level = parts[1];

        boolean subjectMatch = "All".equals(selectedSubject) || subject.equals(selectedSubject);
        boolean levelMatch = "All".equals(selectedLevel) || level.equals(selectedLevel);
        return subjectMatch && levelMatch;
    }

    private boolean matchesMonth(Payment payment, String selectedMonth) {
        if ("All".equals(selectedMonth) || selectedMonth == null) {
            return true;
        }

        String paymentDateStr = payment.getDate();
        if (paymentDateStr == null || paymentDateStr.isEmpty()) {
            return false;
        }

        try {
            String[] dateParts = paymentDateStr.split("-");
            if (dateParts.length < 2) {
                return false;
            }

            int monthNumber = Integer.parseInt(dateParts[1]);
            String paymentMonthName = java.time.Month.of(monthNumber)
                    .getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.ENGLISH);

            return paymentMonthName.equals(selectedMonth);

        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            return false;
        }
    }
}