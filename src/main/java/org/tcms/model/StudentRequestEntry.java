package org.tcms.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StudentRequestEntry {
    private final String requestID;
    private final String studentID;
    private final String studentName;
    private final String oldClassID;
    private final String oldSubjectName;
    private final String newClassID;
    private final String newSubjectName;
    private final String reason;
}
