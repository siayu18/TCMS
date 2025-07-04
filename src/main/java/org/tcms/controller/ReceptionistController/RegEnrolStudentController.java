package org.tcms.controller.ReceptionistController;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.tcms.exception.EmptyFieldException;
import org.tcms.exception.ValidationException;
import org.tcms.model.Enrollment;
import org.tcms.model.Student;
import org.tcms.model.TuitionClass;
import org.tcms.service.EnrollmentService;
import org.tcms.service.StudentService;
import org.tcms.service.TuitionClassService;
import org.tcms.utils.AlertUtils;
import org.tcms.utils.Helper;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class RegEnrolStudentController {
    public TextField usernameField;
    public TextField icField;
    public TextField emailField;
    public TextField contactField;
    public TextField addressField;
    public ComboBox levelBox;
    public ComboBox subjectBox1;
    public ComboBox subjectBox2;
    public ComboBox subjectBox3;
    public Button clearButton;
    public Button submitButton;
    public TextField passwordField;
    public DatePicker enrolDatePicker;
    public Label errorLabel;
    public Label subjectErrorLabel;

    private StudentService studentService;
    private TuitionClassService tuitionClassService;
    private EnrollmentService enrollmentService;

    @FXML
    public void initialize() {
        try {
            studentService = new StudentService();
            tuitionClassService = new TuitionClassService();
            enrollmentService = new EnrollmentService();
        } catch (IOException e) {
            AlertUtils.showAlert("Data Loading Issue", "Failed to load data");
            return;
        }

        levelBox.getItems().addAll("Level 1", "Level 2", "Level 3", "Level 4", "Level 5", "Level 6");

        configureActions();
    }

    private void setSubjectBox(ComboBox box, String level) {
        if (level != null) {
            List<TuitionClass> tuitionClasses = tuitionClassService.getClassesFromLevel(level);

            box.getItems().setAll(tuitionClasses);

            box.setCellFactory(cb ->
                    new ListCell<TuitionClass>() {
                        @Override
                        protected void updateItem(TuitionClass tuitionclass, boolean empty) {
                            super.updateItem(tuitionclass, empty);
                            setText(empty || tuitionclass == null ? null : tuitionclass.getClassID() + " - " + tuitionclass.getSubjectName());
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
    }

    private void configureActions() {
        submitButton.setOnAction(e -> {
            try {
                isRequiredEmpty();
                isRequiredValid();

                // Check for duplicate subject selection
                TuitionClass subject1 = (TuitionClass) subjectBox1.getValue();
                TuitionClass subject2 = (TuitionClass) subjectBox2.getValue();
                TuitionClass subject3 = (TuitionClass) subjectBox3.getValue();

                if (Helper.hasDuplicateClassSelections(subject1, subject2, subject3)) {
                    errorLabel.setText("Duplicated class selection, please pick different classes.");
                    errorLabel.setVisible(true);
                    return;
                }

                // Everything ok, then proceed
                errorLabel.setVisible(false);
                addStudentAndEnrollment();
                AlertUtils.showInformation("Successfully Added New Student!", usernameField.getText() + "'s account has been created!");

            } catch (EmptyFieldException | ValidationException ex) {
                errorLabel.setText(ex.getMessage());
                errorLabel.setVisible(true);
            }
        });

        levelBox.setOnAction(e -> {
            // get the class with the same level with student after level box is chosen
            setSubjectBox(subjectBox1, (String) levelBox.getValue());
            setSubjectBox(subjectBox2, (String) levelBox.getValue());
            setSubjectBox(subjectBox3, (String) levelBox.getValue());
            // make subject boxes able to choose and set error message not visible
            subjectBox1.setDisable(false);
            subjectBox2.setDisable(false);
            subjectBox3.setDisable(false);
            subjectErrorLabel.setVisible(false);
        });

        clearButton.setOnAction(e -> clearAll());
    }

    private void isRequiredEmpty() throws EmptyFieldException {
        if (Helper.validateFieldNotEmpty(usernameField) ||
                Helper.validateFieldNotEmpty(icField) ||
                Helper.validateFieldNotEmpty(emailField) ||
                Helper.validateFieldNotEmpty(contactField) ||
                Helper.validateFieldNotEmpty(addressField) ||
                Helper.validateComboBoxNotEmpty(levelBox) ||
                Helper.validateDatePickerNotEmpty(enrolDatePicker)) {
            throw new EmptyFieldException("Required field(s) with indication (*) is empty!");
        }
    }

    private void isRequiredValid() throws ValidationException {
        if (Helper.validatePassword(passwordField.getText())) {
            throw new ValidationException("Password should be more than 8 characters\nand contain at least 1 uppercase, lowercase, digit\nand special character.");
        }

        if (!Helper.validateIC(icField.getText())) {
            throw new ValidationException("IC Format is invalid!");
        }

        if (!Helper.validateEmail(emailField.getText())) {
            throw new ValidationException("Email is invalid!");
        }

        if (!Helper.validateContact(contactField.getText())) {
            throw new ValidationException("Contact is invalid!");
        }
    }

    private void clearAll () {
        usernameField.clear();
        icField.clear();
        emailField.clear();
        contactField.clear();
        addressField.clear();
        passwordField.clear();
        levelBox.setValue(null);
        enrolDatePicker.setValue(null);
        errorLabel.setVisible(false);

        // to avoid subjects before clearing still exist
        subjectBox1.setValue(null);
        subjectBox2.setValue(null);
        subjectBox3.setValue(null);
        subjectBox1.setDisable(true);
        subjectBox2.setDisable(true);
        subjectBox3.setDisable(true);
        subjectErrorLabel.setVisible(true);
    }

    private void addStudentAndEnrollment() {
        // Add a new student
        Student student = new Student(
                Helper.generateAccountID(),
                usernameField.getText(),
                passwordField.getText(),
                "Student",
                icField.getText(),
                emailField.getText(),
                contactField.getText(),
                addressField.getText(),
                levelBox.getValue().toString()
        );

        studentService.addStudent(student);

        // Check selection and add new enrollment
        TuitionClass selectedClass1 = (TuitionClass) subjectBox1.getValue();
        TuitionClass selectedClass2 = (TuitionClass) subjectBox2.getValue();
        TuitionClass selectedClass3 = (TuitionClass) subjectBox3.getValue();

        checkAndAddSelection(selectedClass1, enrollmentService, student.getAccountId());
        checkAndAddSelection(selectedClass2, enrollmentService, student.getAccountId());
        checkAndAddSelection(selectedClass3, enrollmentService, student.getAccountId());
    }

    private void checkAndAddSelection(TuitionClass selection, EnrollmentService enrollmentService, String accountID) {
        if (selection != null) {
            enrollmentService.addEnrollment(new Enrollment(
                    UUID.randomUUID().toString(),
                    accountID,
                    enrolDatePicker.getValue(),
                    selection.getClassID()
            ));
        }
    }
}
