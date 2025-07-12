package org.tcms.model;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TuitionClass {
    private final String classID;
    private final String tutorID;
    private final String subjectName;
    private final String information;
    private final String charges;
    private final String day;
    private final String startTime;
    private final String endTime;
    private final String level;
}
