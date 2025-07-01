package org.tcms.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class Enrollment {
    private final String enrollmentID;
    private final String studentID;
    private final LocalDate enrollmentDate;
    private final String classID;
}
