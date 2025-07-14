package org.tcms.controller.StudentController;

import com.jfoenix.controls.JFXButton;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import org.tcms.exception.EmptyFieldException;
import org.tcms.exception.ValidationException;
import org.tcms.model.Enrollment;
import org.tcms.model.TuitionClass;
import org.tcms.service.EnrollmentService;
import org.tcms.service.TuitionClassService;
import org.tcms.utils.AlertUtils;
import org.tcms.utils.Helper;
import org.tcms.utils.Session;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TransferRequestController {
    //FXML Components
    @FXML private TextField reasonField;
    @FXML private Label errorLabel;
    @FXML private ComboBox selectOldSub;
    @FXML private  ComboBox selectNewSub;
    @FXML private JFXButton clearBtn;
    @FXML private JFXButton submitBtn;

    //Services
    private EnrollmentService enrollmentService;
    private TuitionClassService tuitionClassService;
    private String currentStudentId;

    private void initialize() {
        try {
            enrollmentService = new EnrollmentService();
            tuitionClassService = new TuitionClassService();

            // Get current student ID from Session (no need for custom auth logic)
            currentStudentId = Session.getCurrentUserID();

            // Validate: Ensure a student is logged in
            if (currentStudentId == null) {
                AlertUtils.showAlert("Error", "No student logged in. Please log in first.");
                return;
            }

            loadExistingSubjects();
            configureActions();
        } catch (IOException e) {
            AlertUtils.showAlert("Error", "Failed to load data: " + e.getMessage());
        }
    }

    private void configureActions(){
        submitBtn.setOnAction(e -> {
            try {
                isRequiredEmpty();

                errorLabel.setVisible(false);
                AlertUtils.showInformation("Successfully Submitted Transfer Request!", "Your request to transfer to: " + selectNewSub.getValue().toString() + " has been submitted, check communication hub for updates!");
                clearAll();

            } catch (EmptyFieldException | ValidationException ex) {
                errorLabel.setText(ex.getMessage());
                errorLabel.setVisible(true);
            }
        });

    };


    private void isRequiredEmpty() throws EmptyFieldException {
        boolean hasEmptyFields = Helper.validateFieldNotEmpty(reasonField) ||
                Helper.validateComboBoxNotEmpty(selectOldSub) ||
                Helper.validateComboBoxNotEmpty(selectNewSub);

        if (hasEmptyFields) {
            throw new EmptyFieldException("Required field(s) with indication (*) is empty!");
        }
    }

    private void loadExistingSubjects() {
        try {
            // Get all enrollments for the current student
            List<Enrollment> studentEnrollments = enrollmentService.getAllEnrollment().stream()
                    .filter(e -> e.getStudentID().equals(currentStudentId))
                    .collect(Collectors.toList());

            // Map each enrollment to its corresponding TuitionClass
            List<TuitionClass> existingClasses = studentEnrollments.stream()
                    .map(e -> tuitionClassService.getClassByID(e.getClassID()))
                    .filter(Objects::nonNull) // Filter out null classes (if any)
                    .collect(Collectors.toList());

            // Set items in the combobox
            selectOldSub.setItems(FXCollections.observableArrayList(existingClasses));

            // Customize display to show subject names
            selectOldSub.setCellFactory(param -> new javafx.scene.control.ListCell<TuitionClass>() {
                @Override
                protected void updateItem(TuitionClass item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getSubjectName() + " (" + item.getLevel() + ")");
                    }
                }
            });

            // Do the same for the button cell (the displayed value when the combobox is closed)
            selectOldSub.setButtonCell((ListCell) selectOldSub.getCellFactory().call(null));

        } catch (Exception e) {
            AlertUtils.showAlert("Error", "Failed to load existing subjects: " + e.getMessage());
        }
    }

    private void loadAvailableNewSubjects(String studentLevel) {
        try {
            // Get all classes at the student's level
            List<TuitionClass> allClassesAtLevel = tuitionClassService.getAllClasses().stream()
                    .filter(cls -> cls.getLevel().equals(studentLevel))
                    .collect(Collectors.toList());

            // Get IDs of classes the student is already enrolled in
            List<String> existingClassIds = enrollmentService.getAllEnrollment().stream()
                    .filter(e -> e.getStudentID().equals(currentStudentId))
                    .map(Enrollment::getClassID)
                    .collect(Collectors.toList());

            // Filter out classes the student is already enrolled in
            List<TuitionClass> availableClasses = allClassesAtLevel.stream()
                    .filter(cls -> !existingClassIds.contains(cls.getClassID()))
                    .collect(Collectors.toList());

            // Set items in the combobox
            selectNewSub.setItems(FXCollections.observableArrayList(availableClasses));

            // Customize display to show subject names
            selectNewSub.setCellFactory(param -> new javafx.scene.control.ListCell<TuitionClass>() {
                @Override
                protected void updateItem(TuitionClass item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getSubjectName() + " (" + item.getLevel() + ")");
                    }
                }
            });

            // Do the same for the button cell
            selectNewSub.setButtonCell((ListCell) selectNewSub.getCellFactory().call(null));

        } catch (Exception e) {
            AlertUtils.showAlert("Error", "Failed to load available subjects: " + e.getMessage());
        }
    }

    private void clearAll() {
        reasonField.clear();
        selectNewSub.setValue(null);
        selectOldSub.setValue(null);
        errorLabel.setVisible(false);
    }
}
