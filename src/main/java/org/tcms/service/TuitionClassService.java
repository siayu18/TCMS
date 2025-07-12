package org.tcms.service;

import org.tcms.model.TuitionClass;
import org.tcms.utils.FileHandler;
import org.tcms.utils.Session;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TuitionClassService {
    private final FileHandler classFile;

    public TuitionClassService() throws IOException {
        classFile = new FileHandler("tuitionclass.csv", Arrays.asList("ClassID","TutorID","SubjectName","Information","Charges","Schedule"));
    }

    public List<TuitionClass> getAllClasses() {
        return classFile.readAll().stream()
                .map(row -> new TuitionClass(
                        row.get("ClassID"),
                        row.get("TutorID"),
                        row.get("SubjectName"),
                        row.get("Information"),
                        row.get("Charges"),
                        row.get("Schedule"),
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
                        row.get("Schedule"),
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
                        row.get("Schedule"),
                        row.get("Level")
                ))
                .collect(Collectors.toList());
    }

    public TuitionClass getClassByID(String classID) {
        return getAllClasses().stream()
                .filter(cls -> cls.getClassID().equals(classID))
                .findFirst()
                .orElse(null);
    }
}
