package org.tcms.controller.ReceptionistController;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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
    public Label emptyLabel;
    public Label errorLabel;

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
            errorLabel.setText("Failed to load data.");
            errorLabel.setVisible(true);
            return;
        }

        levelBox.getItems().addAll("Level 1", "Level 2", "Level 3", "Level 4", "Level 5", "Level 6");
        preventSubjectWithoutLevel(subjectBox1);
        preventSubjectWithoutLevel(subjectBox2);
        preventSubjectWithoutLevel(subjectBox3);
        configureActions();
    }

    private void preventSubjectWithoutLevel(ComboBox<TuitionClass> subjectBox) {
        subjectBox.setOnShowing(new EventHandler<Event>()  {
            @Override
            public void handle(Event event) {
                if (levelBox.getValue() == null) {
                    event.consume(); // cancel pop up
                    AlertUtils.showAlert("Select Level First", "Please select a Level before choosing subjects.");
                }
            }
        });
    }

    private void setSubjectBox(ComboBox box, String level) {
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

    private void configureActions() {
        submitButton.setOnAction(e -> {
            boolean isUsernameEmpty = Helper.validateFieldNotEmpty(usernameField, emptyLabel, "Required field(s) with indication (*) is empty!");
            boolean isICEmpty = Helper.validateFieldNotEmpty(icField, emptyLabel, "Required field(s) with indication (*) is empty!");
            boolean isEmailEmpty = Helper.validateFieldNotEmpty(emailField, emptyLabel, "Required field(s) with indication (*) is empty!");
            boolean isContactEmpty = Helper.validateFieldNotEmpty(contactField, emptyLabel, "Required field(s) with indication (*) is empty!");
            boolean isAddressEmpty = Helper.validateFieldNotEmpty(addressField, emptyLabel, "Required field(s) with indication (*) is empty!");
            boolean isLevelEmpty = Helper.validateComboBoxNotEmpty(levelBox, emptyLabel, "Required field(s) with indication (*) is empty!");
            boolean isEnrollDateEmpty = Helper.validateDatePickerNotEmpty(enrolDatePicker, emptyLabel, "Required field(s) with indication (*) is empty!");
            if (isUsernameEmpty || isICEmpty || isEmailEmpty || isContactEmpty || isAddressEmpty || isLevelEmpty || isEnrollDateEmpty) {
                emptyLabel.setVisible(true);
                return;
            }

            boolean isPasswordValid = Helper.validatePassword(passwordField.getText());
            if (!isPasswordValid) {
                errorLabel.setText("Password should be more than 8 characters\nand contain at least 1 uppercase, lowercase, digit\nand special character.");
                errorLabel.setVisible(true);
                emptyLabel.setVisible(false);
                return;
            }

            boolean isICValid = Helper.validateIC(icField.getText());
            if (!isICValid) {
                errorLabel.setText("IC Format is invalid!");
                errorLabel.setVisible(true);
                emptyLabel.setVisible(false);
                return;
            }

            boolean isEmailValid = Helper.validateEmail(emailField.getText());
            if (!isEmailValid) {
                errorLabel.setText("Email is invalid!");
                errorLabel.setVisible(true);
                emptyLabel.setVisible(false);
                return;
            }

            boolean isContactValid = Helper.validateContact(contactField.getText());
            if(!isContactValid) {
                errorLabel.setText("Contact is invalid!");
                errorLabel.setVisible(true);
                emptyLabel.setVisible(false);
                return;
            }

            emptyLabel.setVisible(false);
            errorLabel.setVisible(false);
            addStudentAndEnrollment();
            AlertUtils.showInformation("Successfully Added New Student!", usernameField.getText() + "'s account has been created!");
        });

        levelBox.setOnAction(e -> {
            setSubjectBox(subjectBox1, (String) levelBox.getValue());
            setSubjectBox(subjectBox2, (String) levelBox.getValue());
            setSubjectBox(subjectBox3, (String) levelBox.getValue());
        });

        clearButton.setOnAction(e -> clearAll());
    }

    private void clearAll () {
        usernameField.clear();
        icField.clear();
        emailField.clear();
        contactField.clear();
        addressField.clear();
        passwordField.clear();
        levelBox.setValue(null);
        subjectBox1.setValue(null);
        subjectBox2.setValue(null);
        subjectBox3.setValue(null);
        enrolDatePicker.setValue(null);
        errorLabel.setVisible(false);
        emptyLabel.setVisible(false);
    }

    private void addStudentAndEnrollment() {
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
