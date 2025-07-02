package org.tcms.controller.AdminController;

import com.jfoenix.controls.*;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.tcms.service.UserService;
import org.tcms.utils.AlertUtils;
import org.tcms.utils.Helper;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RegStaffController {
    // FXML components
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField visiblePasswordField;
    @FXML private JFXComboBox<String> selectRole;
    @FXML private VBox tutorSelections;
    @FXML private JFXComboBox<String> selectSubject;
    @FXML private JFXComboBox<String> selectLevel;
    @FXML private JFXCheckBox showPassword;
    @FXML private JFXButton clearBtn;
    @FXML private JFXButton submitBtn;
    @FXML private Label errorLabel;
    @FXML private Label emptyLabel;

    // Services
    private UserService userService;

    // Predefined options
    private final List<String> LEVELS = Arrays.asList("Form 1", "Form 2", "Form 3", "Form 4", "Form 5");
    private final List<String> SUBJECTS = Arrays.asList(
            "Mathematics", "English", "Science", "History", "Geography",
            "Physics", "Chemistry", "Biology", "Bahasa Malaysia"
    );

    @FXML
    public void initialize() {
        try {
            userService = new UserService();
        } catch (IOException e) {
            showError("System initialization failed: " + e.getMessage());
            disableForm();
            return;
        }

        setupUIComponents();
    }

    private void setupUIComponents() {
        // Initialize role dropdown
        selectRole.getItems().addAll("Admin", "Receptionist", "Tutor");
        selectRole.valueProperty().addListener((obs, oldVal, newVal) -> {
            tutorSelections.setVisible("Tutor".equals(newVal));
        });

        // Initialize level and subject dropdowns
        selectLevel.getItems().addAll(LEVELS);
        selectSubject.getItems().addAll(SUBJECTS);

        // Password visibility toggle
        showPassword.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            if (isSelected) {
                visiblePasswordField.setText(passwordField.getText());
                visiblePasswordField.setVisible(true);
                passwordField.setVisible(false);
            } else {
                passwordField.setText(visiblePasswordField.getText());
                passwordField.setVisible(true);
                visiblePasswordField.setVisible(false);
            }
        });

        // Button actions
        clearBtn.setOnAction(e -> clearForm());
        submitBtn.setOnAction(e -> submitForm());

        // Initial state
        visiblePasswordField.setVisible(false);
        tutorSelections.setVisible(false);
    }

    private void clearForm() {
        usernameField.clear();
        passwordField.clear();
        visiblePasswordField.clear();
        selectRole.setValue(null);
        selectSubject.setValue(null);
        selectLevel.setValue(null);
        showPassword.setSelected(false);
        errorLabel.setVisible(false);
        emptyLabel.setVisible(false);
        tutorSelections.setVisible(false);
    }

    private void submitForm() {
        // Get input values
        String username = usernameField.getText().trim();
        String password = getPassword();
        String role = selectRole.getValue();

        // Validate inputs
        if (!validateInputs(username, password, role)) {
            return;
        }

        try {
            // Get tutor assignments if applicable
            List<String> levels = role.equals("Tutor") ?
                    Collections.singletonList(selectLevel.getValue()) :
                    Collections.emptyList();

            List<String> subjects = role.equals("Tutor") ?
                    Collections.singletonList(selectSubject.getValue()) :
                    Collections.emptyList();


            // Success feedback
            AlertUtils.showInformation("Success",
                    role + " account created for " + username);
            clearForm();
        } catch (Exception e) {
            showError("Registration failed: " + e.getMessage());
        }
    }

    private String getPassword() {
        return showPassword.isSelected() ?
                visiblePasswordField.getText() :
                passwordField.getText();
    }

    private boolean validateInputs(String username, String password, String role) {
        // Reset error states
        errorLabel.setVisible(false);
        emptyLabel.setVisible(false);
        boolean isValid = true;

        // Validate required fields
        if (username.isEmpty()) {
            emptyLabel.setText("Username is required");
            emptyLabel.setVisible(true);
            isValid = false;
        }

        if (password.isEmpty()) {
            emptyLabel.setText("Password is required");
            emptyLabel.setVisible(true);
            isValid = false;
        }

        if (role == null) {
            emptyLabel.setText("Role selection is required");
            emptyLabel.setVisible(true);
            isValid = false;
        }

        // Validate tutor-specific fields
        if ("Tutor".equals(role)) {
            if (selectLevel.getValue() == null) {
                emptyLabel.setText("Level selection is required for tutors");
                emptyLabel.setVisible(true);
                isValid = false;
            }

            if (selectSubject.getValue() == null) {
                emptyLabel.setText("Subject selection is required for tutors");
                emptyLabel.setVisible(true);
                isValid = false;
            }
        }

        // Check password strength
        if (!password.isEmpty() && !Helper.validatePassword(password)) {
            errorLabel.setText("Password must be 8+ characters with uppercase, lowercase, digit, and special character");
            errorLabel.setVisible(true);
            isValid = false;
        }
        return isValid;
    }

    private void showError(String message) {
        AlertUtils.showAlert("Error", message);
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    private void disableForm() {
        usernameField.setDisable(true);
        passwordField.setDisable(true);
        visiblePasswordField.setDisable(true);
        selectRole.setDisable(true);
        selectSubject.setDisable(true);
        selectLevel.setDisable(true);
        submitBtn.setDisable(true);
    }
}