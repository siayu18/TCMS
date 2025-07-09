package org.tcms.controller.TutorController;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
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
import org.tcms.utils.MappingUtils;


import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ViewStudentListController {

    public VBox tableBox;
    @FXML private ComboBox<TuitionClass> chooseClassBox;
    @FXML TableView<Student> studentTable;
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
        setupChooseClassBox();
        configureTable();
        configureActions();
    }

    private void setupChooseClassBox() {
        tuitionClass = tuitionClassService.getClassesFromTutor();
        chooseClassBox.getItems().setAll(tuitionClass);

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
