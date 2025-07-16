package org.tcms.service;

import org.tcms.model.TuitionClass;
import org.tcms.model.Tutor;
import org.tcms.utils.FileHandler;
import org.tcms.utils.Helper;
import org.tcms.utils.Session;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TuitionClassService {
    private final FileHandler classFile;
    private final FileHandler subjectFile;

    public TuitionClassService() throws IOException {
        classFile = new FileHandler("tuitionclass.csv", Arrays.asList("ClassID","TutorID","SubjectName","Information","Charges","Day","StartTime","EndTime","Level"));
        subjectFile = new FileHandler("tutor.csv", Arrays.asList("TutorID","AssignedSubjects","AssignedLevels"));
    }

    public List<TuitionClass> getAllClasses() {
        return classFile.readAll().stream()
                .map(row -> new TuitionClass(
                        row.get("ClassID"),
                        row.get("TutorID"),
                        row.get("SubjectName"),
                        row.get("Information"),
                        row.get("Charges"),
                        row.get("Day"),
                        row.get("StartTime"),
                        row.get("EndTime"),
                        row.get("Level")
                ))
                .collect(Collectors.toList());
    }

    public List<TuitionClass> getClassesFromTutor() {
        return classFile.readAll().stream()
                .filter(row -> Session.getCurrentUserID().equalsIgnoreCase(row.get("TutorID")))
                .map(row -> new TuitionClass(
                        row.get("ClassID"),
                        row.get("TutorID"),
                        row.get("SubjectName"),
                        row.get("Information"),
                        row.get("Charges"),
                        row.get("Day"),
                        row.get("StartTime"),
                        row.get("EndTime"),
                        row.get("Level")
                ))
                .collect(Collectors.toList());
    }

    public List<TuitionClass> getClassesFromLevel(String level) {
        return classFile.readAll().stream()
                .filter(row -> level.equalsIgnoreCase(row.get("Level")))
                .map(row -> new TuitionClass(
                        row.get("ClassID"),
                        row.get("TutorID"),
                        row.get("SubjectName"),
                        row.get("Information"),
                        row.get("Charges"),
                        row.get("Day"),
                        row.get("StartTime"),
                        row.get("EndTime"),
                        row.get("Level")
                ))
                .collect(Collectors.toList());
    }


    public List<Tutor> getAssignedSubjectsForTutor(){
        return subjectFile.readAll().stream()
                .filter(row -> Session.getCurrentUserID().equalsIgnoreCase(row.get("TutorID")))
                .map(row -> new Tutor(
                        row.get("AssignedLevels"),
                        row.get("AssignedSubjects")
                ))
                .collect(Collectors.toList());
    }

    public void addClass(String TutorID, String subjectName, String information, String charges, String day, String startTime, String endTime, String level) {
        classFile.append(Map.of(
                "ClassID", Helper.generateClassID(),
                "TutorID", TutorID,
                "SubjectName", subjectName,
                "Information", information,
                "Charges", charges,
                "Day", day,
                "StartTime", startTime,
                "EndTime", endTime,
                "Level", level
        ));
    }

    public TuitionClass getClassByID(String classID) {
        return getAllClasses().stream()
                .filter(row -> row.getClassID().equals(classID))
                .findFirst()
                .orElse(null);
    }


    public void updateClass (String classID, String newInfo, String newCharges, String newDay, String newStartTime, String newEndTime) {
        List<Map<String, String>> rows = classFile.readAll();

        for (Map<String, String> row : rows) {
            if (classID.equals(row.get("ClassID"))) {
                row.put("Information", newInfo);
                row.put("Charges", newCharges);
                row.put("Day", newDay);
                row.put("StartTime", newStartTime);
                row.put("EndTime", newEndTime);
                break;
            }
        }
        classFile.overwriteAll(rows);
    }

    public void deleteClass(String classID) {
        List<Map<String, String>> rows = classFile.readAll();
        rows.removeIf(row -> classID.equals(row.get("ClassID")));
        classFile.overwriteAll(rows);
    }
}
