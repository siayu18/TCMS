package org.tcms.service;

import org.tcms.model.ClassRecord;
import org.tcms.utils.FileHandler;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ClassRecordService {
    private final FileHandler classRecordFile;

    public ClassRecordService() throws IOException {
        classRecordFile = new FileHandler("classrecord.csv", Arrays.asList("ClassRecID","StudentID","EnrolmentDate","ClassID"));
    }

    public List<ClassRecord> getAllClasses() {
        return classRecordFile.readAll().stream()
                .map(row -> new ClassRecord(
                        row.get("ClassRecID"),
                        row.get("StudentID"),
                        LocalDate.parse(row.get("EnrolmentDate")),
                        row.get("ClassID")
                ))
                .collect(Collectors.toList());
    }

    public void addClassRecord(ClassRecord record) {
        classRecordFile.append(Map.of(
                "ClassRecID", record.getClassRecId(),
                "StudentID", record.getStudentId(),
                "EnrolmentDate", record.getEnrolmentDate().toString(),
                "ClassID", record.getClassId()
        ));
    }

    public List<ClassRecord> getByStudent(String studentId) {
        return getAllClasses().stream()
                .filter(record -> record.getStudentId().equals(studentId))
                .collect(Collectors.toList());
    }

    public void deleteClassRecord(String classRecId) {
        var rows = classRecordFile.readAll();
        rows.removeIf(r -> r.get("ClassRecID").equals(classRecId));
        classRecordFile.overwriteAll(rows);
    }
}
