package org.tcms.controller.TutorController;

import com.jfoenix.controls.JFXComboBox;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import org.tcms.model.Enrollment;
import org.tcms.model.Student;
import org.tcms.model.TuitionClass;
import org.tcms.service.EnrollmentService;
import org.tcms.service.StudentService;
import org.tcms.service.TuitionClassService;
import org.tcms.utils.AlertUtils;
import org.tcms.utils.ComponentUtils;
import org.tcms.utils.MappingUtils;


import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ViewStudentListController {

    @FXML private VBox tableBox;
    @FXML private JFXComboBox<TuitionClass> chooseClassBox;
    @FXML private TableView<Student> studentTable;
    @FXML private TableColumn<Student, String> studentIDColumn;
    @FXML private TableColumn<Student, String> studentNameColumn;
    @FXML private TableColumn<Student, String> levelColumn;
    @FXML private TableColumn<Student, String> contactColumn;
    @FXML private TableColumn<Student, String> emailColumn;

    private TuitionClass selectedTuitionClass;
    private Map<String, Student> student;
    private List<TuitionClass> tuitionClass;
    private TuitionClassService tuitionClassService;
    private StudentService studentService;
    private List<Enrollment> enrollment;
    private EnrollmentService enrollmentStudent;

    public void initialize() {
        try {
            enrollmentStudent = new EnrollmentService();
            studentService = new StudentService();
            tuitionClassService = new TuitionClassService();
        } catch (IOException e) {
            AlertUtils.showAlert("Data Loading Issue", "Failed to load data");
            return;
        }

        student = studentService.getAllStudents().stream()
                .collect(Collectors.toMap(
                        Student::getAccountId,
                        student -> student
                ));
        enrollment = enrollmentStudent.getAllEnrollment();
        tuitionClass = tuitionClassService.getClassesFromTutor();

        ComponentUtils.configureClassBox(chooseClassBox, tuitionClass);
        configureTable();
        configureActions();
    }

    private void configureActions() {
        chooseClassBox.setOnAction(e -> {
            selectedTuitionClass = chooseClassBox.getValue();
            if (selectedTuitionClass != null){
                tableBox.setVisible(true);
                loadStudentTable(selectedTuitionClass);
            }
        });
    }

    private void configureTable(){
        studentIDColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getAccountId()));
        studentNameColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getUsername()));
        levelColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getLevel()));
        contactColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getContactNumber()));
        emailColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getEmail()));
    }

    private void loadStudentTable(TuitionClass tuitionClass){
        ObservableList<Student> viewList = FXCollections.observableArrayList(
                MappingUtils.mapStudentForClasses(tuitionClass, student, enrollment));
        studentTable.setItems(viewList);
    }
}
