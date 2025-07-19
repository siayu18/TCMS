package org.tcms.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MonthlyReportEntry {
    private String subject; // TuitionClass
    private String level; // TuitionClass
    private double totalIncome; // Aggregated from Payment amounts
}