package org.tcms.service;

import org.tcms.model.TuitionClass;
import org.tcms.utils.FileHandler;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TuitionClassService {
    private final FileHandler classFile;

    public TuitionClassService() {
        classFile = new FileHandler("tuitionclass.csv", Arrays.asList("ClassID","SubjectName","Information","Charges","Schedule"));
    }

    public List<TuitionClass> getAllClasses() {
        return classFile.readAll().stream()
                .map(row -> new TuitionClass(
                        row.get("ClassID"),
                        row.get("SubjectName"),
                        row.get("Information"),
                        row.get("Charges"),
                        row.get("Schedule")
                ))
                .collect(Collectors.toList());
    }
}
