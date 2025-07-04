package org.tcms.controller.SystemController;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.tcms.exception.EmptyFieldException;
import org.tcms.exception.ValidationException;
import org.tcms.service.UserService;
import org.tcms.utils.AlertUtils;
import org.tcms.utils.Helper;
import org.tcms.utils.Session;

import java.io.IOException;

public class EditProfileController {
    public TextField usernameField;
    public PasswordField passwordField;
    public PasswordField confirmPasswordField;
    public CheckBox showPasswordCheckBox;
    public Label errorLabel;
    public Button enterButton;
    public TextField visiblePasswordField;
    public TextField visibleConfirmPasswordField;

    private UserService userService;

    @FXML
    public void initialize() {
        try {
            userService = new UserService();
        } catch (IOException e) {
            AlertUtils.showAlert("Data Loading Issue", "Failed to load data");
            return;
        }

        enterButton.setDefaultButton(true);
        visiblePasswordField.textProperty().bindBidirectional(passwordField.textProperty());
        visibleConfirmPasswordField.textProperty().bindBidirectional(confirmPasswordField.textProperty());
        configureActions();
    }

    private void configureActions() {
        showPasswordCheckBox.setOnAction(e -> handleShowPassword());

        enterButton.setOnAction(e -> {
            try {
                String newUsername = usernameField.getText();
                String newPassword = passwordField.getText();
                String confirmPassword = confirmPasswordField.getText();

                Helper.isUsernamePasswordEmpty(usernameField, passwordField, errorLabel);
                isPasswordValid(newPassword, confirmPassword);

                errorLabel.setVisible(false);
                userService.updateUser(Session.getCurrentUserID(), newUsername, newPassword);
                AlertUtils.showInformation("Successfully Updated User!", usernameField.getText() + "'s account has been updated!");

            } catch (EmptyFieldException | ValidationException ex) {
                errorLabel.setText(ex.getMessage());
                errorLabel.setVisible(true);
            }
        });
    }

    private void handleShowPassword() {
        boolean show = showPasswordCheckBox.isSelected();

        // Toggle visibility
        visiblePasswordField.setVisible(show);
        visiblePasswordField.setManaged(show);
        visibleConfirmPasswordField.setVisible(show);
        visibleConfirmPasswordField.setManaged(show);

        passwordField.setVisible(!show);
        passwordField.setManaged(!show);
        confirmPasswordField.setVisible(!show);
        confirmPasswordField.setManaged(!show);
    }

    private void isPasswordValid(String newPassword, String confirmPassword) {
        if (Helper.validatePassword(passwordField.getText())) {
            throw new ValidationException("Password should be more than 8 characters\nand contain at least 1 uppercase, lowercase, digit\nand special character.");
        }

        if (!newPassword.equals(confirmPassword)) {
            throw new ValidationException("Password not matched.");
        }
    }

}
