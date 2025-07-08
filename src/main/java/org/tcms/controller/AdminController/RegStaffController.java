package org.tcms.controller.AdminController;

import com.jfoenix.controls.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.tcms.exception.EmptyFieldException;
import org.tcms.exception.ValidationException;
import org.tcms.model.*;
import org.tcms.service.TutorService;
import org.tcms.service.UserService;
import org.tcms.utils.AlertUtils;
import org.tcms.utils.Helper;
import java.io.IOException;

public class RegStaffController {
    // FXML components
    @FXML private TextField usernameField, visiblePasswordField, selectSubject;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> selectRole;
    @FXML private VBox tutorSelections;
    @FXML private ComboBox<String> selectLevel;
    @FXML private JFXCheckBox showPassword;
    @FXML private JFXButton clearBtn;
    @FXML private JFXButton submitBtn;
    @FXML private Label errorLabel;
    @FXML private Label emptyLabel;

    // Services
    private UserService userService;
    private TutorService tutorService;

    @FXML
    public void initialize() {
        try {
            userService = new UserService();
            tutorService = new TutorService();
        } catch (IOException e) {
            AlertUtils.showAlert("An error occurred while loading the data", "Failed to load data");
            disableForm();
            return;
        }

        selectLevel.getItems().addAll("Level 1", "Level 2", "Level 3", "Level 4", "Level 5", "Level 6");
        selectRole.getItems().addAll("Admin", "Tutor", "Receptionist");

        configureActions();
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

    private void configureActions() {
        tutorSelections.setVisible(false);
        visiblePasswordField.textProperty().bindBidirectional(passwordField.textProperty());

        // Submission btn logic
        submitBtn.setOnAction(e -> {
            try {
                isRequiredEmpty();
                isRequiredValid();

                errorLabel.setVisible(false);
                AlertUtils.showInformation("Successfully Registered New Staff!", usernameField.getText() + "'s account has been created!");
                addStaff();
                clearAll();

            } catch (EmptyFieldException | ValidationException ex) {
                errorLabel.setText(ex.getMessage());
                errorLabel.setVisible(true);
            }
        });

        // Shows select lvl & sub if selected tutor role
        selectRole.valueProperty().addListener((obs, oldVal, newVal) ->
                tutorSelections.setVisible(String.valueOf(newVal).equals("Tutor")));

        // Toggle visibility of password field
        showPassword.setOnAction(e -> {
            boolean show = showPassword.isSelected();
            visiblePasswordField.setVisible(show);
            passwordField.setVisible(!show);
        });

        clearBtn.setOnAction(e -> clearAll());
    }

    private void isRequiredEmpty() throws EmptyFieldException {
        boolean hasEmptyFields = Helper.validateFieldNotEmpty(usernameField) ||
                Helper.validateFieldNotEmpty(passwordField) ||
                Helper.validateComboBoxNotEmpty(selectRole);

        String selectedRole = selectRole.getValue();
        if ("Tutor".equals(selectedRole)) {
            hasEmptyFields |= Helper.validateComboBoxNotEmpty(selectLevel) ||
                    Helper.validateFieldNotEmpty(selectSubject);
        }

        if (hasEmptyFields) {
            throw new EmptyFieldException("Required field(s) with indication (*) is empty!");
        }
    }

    private void isRequiredValid() throws ValidationException {
        if (!Helper.validatePassword(passwordField.getText())) {
            throw new ValidationException("Password should be more than 8 characters\nand contain at least 1 uppercase, lowercase, digit\nand special character.");
        }
    }


    private void addStaff(){
        String selectedRole = selectRole.getValue();
        String username = usernameField.getText();
        String password = showPassword.isSelected() ? visiblePasswordField.getText() : passwordField.getText();
        String accountID = Helper.generateAccountID();

        User user;
        switch(selectedRole){
            case "Admin":
                user = new Admin(accountID, username,password, "Admin");
                userService.addUser(user);
                break;
            case "Receptionist":
                user = new Receptionist(accountID, username, password, "Receptionist");
                userService.addUser(user);
                break;
            case "Tutor":
                String selectedSubject = selectSubject.getText().trim();
                String selectedLevel = selectLevel.getValue();

                user = new Tutor(accountID, username, password, "Tutor", selectedLevel, selectedSubject);
                tutorService.addTutor((Tutor) user);
                break;
        }
    }

    private void clearAll() {
        usernameField.clear();
        passwordField.clear();
        visiblePasswordField.clear();
        selectRole.setValue(null);
        selectLevel.setValue(null);
        showPassword.setSelected(false);
        errorLabel.setVisible(false);
        emptyLabel.setVisible(false);
        tutorSelections.setVisible(false);
        selectSubject.clear();
    }

}