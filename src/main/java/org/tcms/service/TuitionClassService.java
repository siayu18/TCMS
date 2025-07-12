package org.tcms.service;

import org.tcms.model.TuitionClass;
import org.tcms.utils.FileHandler;
import org.tcms.utils.Session;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TuitionClassService {
    private final FileHandler classFile;

    public TuitionClassService() throws IOException {
        classFile = new FileHandler("tuitionclass.csv", Arrays.asList("ClassID","TutorID","SubjectName","Information","Charges","Day","StartTime","EndTime","Level"));
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

    public void addClass(TuitionClass tuitionClass) {
        classFile.append(Map.of(
                "ClassID", tuitionClass.getClassID(),
                "TutorID", tuitionClass.getTutorID(),
                "SubjectName", tuitionClass.getSubjectName(),
                "Information", tuitionClass.getInformation(),
                "Charges", tuitionClass.getCharges(),
                "Day", tuitionClass.getDay(),
                "StartTime", tuitionClass.getStartTime(),
                "EndTime", tuitionClass.getEndTime(),
                "Level", tuitionClass.getLevel()
        ));
    }

    public TuitionClass getClassByID(String classID) {
        return getAllClasses().stream()
                .filter(row -> row.getClassID().equals(classID))
                .findFirst()
                .orElse(null);
    }
}
