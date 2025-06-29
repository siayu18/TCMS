package org.tcms.utils;

import com.jfoenix.controls.JFXComboBox;
import javafx.scene.control.ListCell;
import javafx.util.StringConverter;
import org.tcms.model.Student;

import java.util.List;

public class ComponentUtils {
    public static void configureStudentBox(JFXComboBox<Student> chooseStudentBox, List<Student> students) {
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
}
