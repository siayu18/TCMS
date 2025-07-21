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
import org.tcms.model.Request;
import org.tcms.model.TuitionClass;
import org.tcms.service.EnrollmentService;
import org.tcms.service.RequestService;
import org.tcms.service.TuitionClassService;
import org.tcms.utils.AlertUtils;
import org.tcms.utils.Helper;
import org.tcms.utils.Session;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class TransferRequestController {
    //FXML Components
    @FXML private TextField reasonField;
    @FXML private Label errorLabel;
    @FXML private ComboBox<TuitionClass> selectOldSub;
    @FXML private ComboBox<TuitionClass> selectNewSub;
    @FXML private JFXButton clearBtn;
    @FXML private JFXButton submitBtn;

    //Services
    private EnrollmentService enrollmentService;
    private TuitionClassService tuitionClassService;
    private RequestService requestService;
    private String currentStudentId;

    @FXML
    private void initialize() {
        try {
            enrollmentService = new EnrollmentService();
            tuitionClassService = new TuitionClassService();
            requestService = new RequestService();

            // Get current student ID from Session
            currentStudentId = Session.getCurrentUserID();

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

                TuitionClass selectedOldClass = selectOldSub.getValue();
                TuitionClass selectedNewClass = selectNewSub.getValue();

                if (selectedOldClass.getClassID().equals(selectedNewClass.getClassID())) {
                    throw new ValidationException("Old and new classes cannot be the same!");
                }

                Request newRequest = new Request(
                        UUID.randomUUID().toString(),
                        currentStudentId,
                        selectedOldClass.getClassID(),
                        selectedNewClass.getClassID(),
                        reasonField.getText()
                );


                requestService.addRequest(newRequest);


                AlertUtils.showInformation("Success",
                        "Transfer request submitted!\n" +
                                "From: " + selectedOldClass.getSubjectName() + "\n" +
                                "To: " + selectedNewClass.getSubjectName()
                );
                clearAll();

            } catch (EmptyFieldException | ValidationException ex) {
                errorLabel.setText(ex.getMessage());
                errorLabel.setVisible(true);
            } catch (Exception ex) {
                AlertUtils.showAlert("Error", "Failed to submit request: " + ex.getMessage());
            }
        });

        selectOldSub.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectNewSub.setDisable(false);
                loadAvailableNewSubjects(newVal.getLevel());
            } else {
                selectNewSub.setDisable(true);
                selectNewSub.setItems(null);
            }
        });
        clearBtn.setOnAction(e -> clearAll());

    };


    private void isRequiredEmpty() throws EmptyFieldException {
        boolean hasEmptyFields = Helper.validateFieldNotEmpty(reasonField) ||
                Helper.validateComboBoxNotEmpty(selectOldSub) ||
                Helper.validateComboBoxNotEmpty(selectNewSub);

        if (hasEmptyFields) {
            throw new EmptyFieldException("Fields cannot be empty!");
        }
    }

    private void loadExistingSubjects() {
        try {
            // Get enrollments for the current student (ensure StudentID matches)
            List<Enrollment> studentEnrollments = enrollmentService.getAllEnrollment().stream()
                    .filter(Objects::nonNull) // Filter out null enrollments
                    .filter(e -> currentStudentId.equals(e.getStudentID()))
                    .toList();

            // Map enrollments to TuitionClass (handle null classes)
            List<TuitionClass> existingClasses = studentEnrollments.stream()
                    .map(e -> {
                        String classId = e.getClassID();
                        return (classId != null) ? tuitionClassService.getClassByID(classId) : null;
                    })
                    .filter(Objects::nonNull)
                    .filter(cls -> {
                        // Ensure class has required fields (prevent NPE in cell factory)
                        return cls.getClassID() != null && cls.getSubjectName() != null && cls.getLevel() != null;
                    })
                    .collect(Collectors.toList());

            // Set items (ensure observable list is not null)
            selectOldSub.setItems(FXCollections.observableArrayList(existingClasses));

            // Cell factory for dropdown list (type-safe)
            selectOldSub.setCellFactory(param -> new ListCell<TuitionClass>() {
                @Override
                protected void updateItem(TuitionClass item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        // Show "Subject (Level)"
                        setText(item.getClassID() + " - " + item.getSubjectName() + " (" + item.getLevel() + ")");
                    }
                }
            });

            // Button cell (displayed when combobox is closed) - no casting
            selectOldSub.setButtonCell(new ListCell<TuitionClass>() {
                @Override
                protected void updateItem(TuitionClass item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getClassID() + " - " + item.getSubjectName() + " (" + item.getLevel() + ")");
                    }
                }
            });

            if (existingClasses.isEmpty()) {
                AlertUtils.showInformation("No Enrollments", "You are not enrolled in any classes.");
            }

        } catch (Exception e) {
            AlertUtils.showAlert("Error", "Failed to load existing subjects: " + e.getMessage());
        }
    }

    private void loadAvailableNewSubjects(String studentLevel) {
        try {
            if (studentLevel == null || studentLevel.isEmpty()) {
                selectNewSub.setItems(FXCollections.observableArrayList());
                return;
            }

            List<TuitionClass> allClassesAtLevel = tuitionClassService.getAllClasses().stream()
                    .filter(Objects::nonNull)
                    .filter(cls -> {
                        return studentLevel.equals(cls.getLevel())
                                && cls.getSubjectName() != null
                                && !cls.getSubjectName().isEmpty();
                    })
                    .toList();

            List<String> existingClassIds = enrollmentService.getAllEnrollment().stream()
                    .filter(Objects::nonNull)
                    .filter(e -> currentStudentId.equals(e.getStudentID()))
                    .map(Enrollment::getClassID)
                    .filter(Objects::nonNull)
                    .toList();

            List<TuitionClass> availableClasses = allClassesAtLevel.stream()
                    .filter(cls -> !existingClassIds.contains(cls.getClassID()))
                    .collect(Collectors.toList());

            selectNewSub.setItems(FXCollections.observableArrayList(availableClasses));

            selectNewSub.setCellFactory(param -> new ListCell<TuitionClass>() {
                @Override
                protected void updateItem(TuitionClass item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getClassID() + " - " + item.getSubjectName() + " (" + item.getLevel() + ")");
                    }
                }
            });

            selectNewSub.setButtonCell(new ListCell<TuitionClass>() {
                @Override
                protected void updateItem(TuitionClass item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getClassID() + " - " + item.getSubjectName() + " (" + item.getLevel() + ")");
                    }
                }
            });

            if (availableClasses.isEmpty()) {
                AlertUtils.showInformation("No Available Classes",
                        "No new classes available at level: " + studentLevel);
            }

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
