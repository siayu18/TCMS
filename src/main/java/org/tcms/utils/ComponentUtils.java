package org.tcms.utils;

import com.jfoenix.controls.JFXComboBox;
import javafx.scene.control.ListCell;
import javafx.util.StringConverter;
import org.tcms.model.Student;
import org.tcms.model.TuitionClass;

import java.util.List;

public class ComponentUtils {
    public static void configureStudentBox (JFXComboBox<Student> chooseStudentBox, List<Student> students) {
        chooseStudentBox.getItems().setAll(students);

        // display "ID, name" in the dropdown
        chooseStudentBox.setCellFactory(cb ->
                new ListCell<Student>() {
                    @Override
                    protected void updateItem(Student student, boolean empty) {
                        super.updateItem(student, empty);
                        setText(empty || student == null ? null : student.getAccountId() + "- " + student.getUsername());
                    }
                }
        );

        chooseStudentBox.setButtonCell(new ListCell<Student>() {
            @Override
            protected void updateItem(Student student, boolean empty) {
                super.updateItem(student, empty);
                setText(empty || student == null ? null : student.getAccountId() + "- " + student.getUsername());
            }
        });
    }

    public static void configureClassBox (JFXComboBox<TuitionClass> chooseClassBox, List<TuitionClass> classes) {
        chooseClassBox.getItems().setAll(classes);

        chooseClassBox.setCellFactory(cb -> new ListCell<TuitionClass>() {
                    @Override
                    protected void updateItem(TuitionClass u, boolean empty) {
                        super.updateItem(u, empty);
                        setText(empty || u == null ? null : u.getClassID() + "- " + u.getSubjectName());
                    }
                }
        );

        chooseClassBox.setButtonCell(new ListCell<TuitionClass>() {
            @Override
            protected void updateItem(TuitionClass u, boolean empty) {
                super.updateItem(u, empty);
                setText(empty || u == null ? null : u.getClassID() + "- " + u.getSubjectName());
            }
        });
    }
}
