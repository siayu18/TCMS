package org.tcms.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class ClassRecord {
    private final String classRecId;
    private final String studentId;
    private final LocalDate enrolmentDate;
    private final String classId;
}
