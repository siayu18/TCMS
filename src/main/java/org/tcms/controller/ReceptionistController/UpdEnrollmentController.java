package org.tcms.controller.ReceptionistController;

import com.jfoenix.controls.JFXComboBox;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.AnchorPane;
import org.tcms.model.Enrollment;
import org.tcms.model.Student;
import org.tcms.model.TuitionClass;
import org.tcms.service.EnrollmentService;
import org.tcms.service.StudentService;
import org.tcms.service.TuitionClassService;
import org.tcms.utils.AlertUtils;
import org.tcms.utils.ComponentUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class UpdEnrollmentController {

    public ComboBox subjectBox1;
    public ComboBox subjectBox2;
    public ComboBox subjectBox3;
    public AnchorPane updatePane;
    public JFXComboBox chooseStudentBox;
    public Label orgSubjectLabel1;
    public Label orgSubjectLabel2;
    public Label orgSubjectLabel3;
    public Button saveBtn;
    public Label errorLabel;

    private Student selectedStudent;
    private TuitionClassService tuitionClassService;
    private EnrollmentService enrollmentService;
    private StudentService studentService;
    private Enrollment ori1, ori2, ori3;

    @FXML
    public void initialize() {
        try {
            tuitionClassService = new TuitionClassService();
            enrollmentService = new EnrollmentService();
            studentService = new StudentService();
        } catch (IOException e) {
            errorLabel.setText("Failed to load data.");
            errorLabel.setVisible(true);
            return;
        }

        List<Student> students = studentService.getAllStudents();
        ComponentUtils.configureStudentBox(chooseStudentBox, students);
        configureActions();
    }

    public void configureActions() {
        saveBtn.setOnAction(e -> {
            if (selectedStudent == null)
                return;

            // check if there is any duplicated selection
            TuitionClass selection1 = (TuitionClass) subjectBox1.getValue();
            TuitionClass selection2 = (TuitionClass) subjectBox2.getValue();
            TuitionClass selection3 = (TuitionClass) subjectBox3.getValue();

            List<String> selectedIDs = new ArrayList<>();
            if (selection1 != null) selectedIDs.add(selection1.getClassID());
            if (selection2 != null) selectedIDs.add(selection2.getClassID());
            if (selection3 != null) selectedIDs.add(selection3.getClassID());

            Set<String> uniqueIDs = new HashSet<>(selectedIDs); // set auto remove duplication

            // means that duplicate occurs since set removes duplicated id
            if (uniqueIDs.size() < selectedIDs.size()) {
                errorLabel.setText("Duplicated class selection, please pick different classes.");
                errorLabel.setVisible(true);
                return;
            }

            // handle adding enrollment logic
            errorLabel.setVisible(false);
            handleSlot(ori1, subjectBox1.getValue(), selectedStudent.getAccountId());
            handleSlot(ori2, subjectBox2.getValue(), selectedStudent.getAccountId());
            handleSlot(ori3, subjectBox3.getValue(), selectedStudent.getAccountId());

            AlertUtils.showInformation("Updated!", "Enrollment updated.");
        });

        chooseStudentBox.setOnAction(e -> {
            selectedStudent = ((Student) chooseStudentBox.getValue());
            if (selectedStudent != null) {
                setSubjectBox(subjectBox1);
                setSubjectBox(subjectBox2);
                setSubjectBox(subjectBox3);
                updatePane.setVisible(true);
                saveBtn.setVisible(true);
                loadOriginalEnrollments();
            }
        });
    }

    public void setSubjectBox(ComboBox box) {
        List<TuitionClass> tuitionClasses = tuitionClassService.getClassesFromLevel(selectedStudent.getLevel());
        box.getItems().setAll(tuitionClasses);

        box.setCellFactory(cb ->
                new ListCell<TuitionClass>() {
                    @Override
                    protected void updateItem(TuitionClass tuitionclass, boolean empty) {
                        super.updateItem(tuitionclass, empty);
                        setText(empty || tuitionclass == null ? null : tuitionclass.getClassID() + "- " + tuitionclass.getSubjectName());
                    }
                }
        );

        box.setButtonCell(new ListCell<TuitionClass>() {
            @Override
            protected void updateItem(TuitionClass item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getClassID() + " - " + item.getSubjectName());
            }
        });
    }

    private void loadOriginalEnrollments() {
        Student selectedStudent = ((Student) chooseStudentBox.getValue());
        List<Enrollment> enrollments = enrollmentService.getByStudent(selectedStudent.getAccountId());

        // check how many classes enrolled before
        ori1 = enrollments.size() > 0 ? enrollments.get(0) : null;
        ori2 = enrollments.size() > 1 ? enrollments.get(1) : null;
        ori3 = enrollments.size() > 2 ? enrollments.get(2) : null;

        Map<String,String> subjectNames = tuitionClassService.getAllClasses().stream()
                .filter(tc -> (ori1 != null && tc.getClassID().equals(ori1.getClassID()))
                        || (ori2  != null && tc.getClassID().equals(ori2.getClassID()))
                        || (ori3  != null && tc.getClassID().equals(ori3.getClassID())))
                .collect(Collectors.toMap(
                        TuitionClass::getClassID,
                        TuitionClass::getSubjectName
                ));

        // set label to original enrolment
        orgSubjectLabel1.setText(ori1 != null ?
                "Org ClassID: " + ori1.getClassID() + " - " + subjectNames.getOrDefault(ori1.getClassID(), "Unknown")
                : "Org ClassID: None");
        orgSubjectLabel2.setText(ori2 != null ?
                "Org ClassID: " + ori2.getClassID() + " - " + subjectNames.getOrDefault(ori2.getClassID(), "Unknown"):
                "Org ClassID: None");
        orgSubjectLabel3.setText(ori3 != null ?
                "Org ClassID: " + ori3.getClassID() + " - " + subjectNames.getOrDefault(ori3.getClassID(), "Unknown"):
                "Org ClassID: None");
    }

    private void handleSlot(Enrollment original, Object comboValue, String studentId) {
        String newClassID = comboValue != null ? ((TuitionClass)comboValue).getClassID(): null;

        // do nothing if both are null
        if (original == null && newClassID == null) {
            return;
        }

        // original and selected are the same then return nothing
        if (original != null && newClassID != null &&  original.getClassID().equals(newClassID)) {
            return;
        }

        // delete old one
        if (original != null) {
            enrollmentService.deleteEnrollment(original.getEnrollmentID());
        }

        // add new one if selected
        if (newClassID != null) {
            enrollmentService.addEnrollment(new Enrollment(
                    UUID.randomUUID().toString(),
                    studentId,
                    LocalDate.now(),
                    newClassID
            ));
        }
    }
}
