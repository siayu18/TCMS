package org.tcms.controller.TutorController;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import org.tcms.model.Student;


public class ViewStudentListController {

    @FXML private ComboBox<String> chooseClassBox;
    @FXML private TableColumn<Student, String> studentIDColumn;
    @FXML private TableColumn<Student, String> studentNameColumn;
    @FXML private TableColumn<Student, String> classColumn;
    @FXML private TableColumn<Student, String> levelColumn;
    @FXML private TableColumn<Student, String> contactColumn;
    @FXML private TableColumn<Student, String> emailColumn;

}
