package org.tcms.controller;

import com.jfoenix.controls.JFXComboBox;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.AnchorPane;
import org.tcms.model.ClassRecord;
import org.tcms.model.Student;
import org.tcms.model.TuitionClass;
import org.tcms.service.ClassRecordService;
import org.tcms.service.StudentService;
import org.tcms.service.TuitionClassService;
import org.tcms.utils.AlertUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

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

    private TuitionClassService tuitionClassService;
    private ClassRecordService classRecordService;
    private StudentService studentService;
    private ClassRecord ori1, ori2, ori3;

    @FXML
    public void initialize() {
        tuitionClassService = new TuitionClassService();
        classRecordService = new ClassRecordService();
        studentService = new StudentService();

        List<Student> students = studentService.getAllStudents();
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

        setSubjectBox(subjectBox1);
        setSubjectBox(subjectBox2);
        setSubjectBox(subjectBox3);
        configureActions();
    }

    public void configureActions() {
        saveBtn.setOnAction(e -> {
            Student student = ((Student) chooseStudentBox.getValue());

            handleSlot(ori1, subjectBox1.getValue(), student.getAccountId());
            handleSlot(ori2, subjectBox2.getValue(), student.getAccountId());
            handleSlot(ori3, subjectBox3.getValue(), student.getAccountId());

            AlertUtils.showSuccessMessage("Updated!", "Enrollment updated.");
        });

        chooseStudentBox.setOnAction(e -> {
            Student selectedUser = ((Student) chooseStudentBox.getValue());
            if (selectedUser != null) {
                updatePane.setVisible(true);
                loadOriginalEnrollments();
            }
        });
    }

    public void setSubjectBox(ComboBox box) {
        List<TuitionClass> tuitionClasses = tuitionClassService.getAllClasses();
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
        List<ClassRecord> records = classRecordService.getByStudent(selectedStudent.getAccountId());

        // check how many classes enrolled before
        ori1 = records.size() > 0 ? records.get(0) : null;
        ori2 = records.size() > 1 ? records.get(1) : null;
        ori3 = records.size() > 2 ? records.get(2) : null;

        // set label to original enrolment
        orgSubjectLabel1.setText(ori1 != null ? "Org ClassID: " + ori1.getClassId() : "Org ClassID: None");
        orgSubjectLabel2.setText(ori2 != null ? "Org ClassID: " + ori2.getClassId() : "Org ClassID: None");
        orgSubjectLabel3.setText(ori3 != null ? "Org ClassID: " + ori3.getClassId() : "Org ClassID: None");
    }

    private void handleSlot(ClassRecord original, Object comboValue, String studentId) {
        String newClassID = comboValue != null ? ((TuitionClass)comboValue).getClassID(): null;

        // do nothing if both are null
        if (original == null && newClassID == null) {
            return;
        }

        // original and selected are the same then return nothing
        if (original != null && newClassID != null &&  original.getClassId().equals(newClassID)) {
            return;
        }

        // delete old one
        if (original != null) {
            classRecordService.deleteClassRecord(original.getClassRecId());
        }

        // add new one if selected
        if (newClassID != null) {
            classRecordService.addClassRecord(new ClassRecord(
                    UUID.randomUUID().toString(),
                    studentId,
                    LocalDate.now(),
                    newClassID
            ));
        }
    }
}
