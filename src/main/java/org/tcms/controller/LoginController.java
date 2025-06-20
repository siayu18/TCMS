package org.tcms.controller;

import javafx.scene.layout.AnchorPane;
import org.tcms.utils.AlertUtils;
import org.tcms.utils.SceneUtils;
import org.tcms.model.User;
import org.tcms.service.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;

public class LoginController {

    private UserService userService;
    private int loginCount;
    @FXML public AnchorPane holderPane;
    @FXML public Label incorrectLabel, failedLabel;
    @FXML public Button loginButton;
    @FXML private TextField usernameField, visiblePasswordField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox showPasswordCheckBox;

    @FXML
    public void initialize() {
        visiblePasswordField.textProperty().bindBidirectional(passwordField.textProperty());

        try {
            userService = new UserService();
        } catch (IOException e) {
            AlertUtils.showAlert("Error", "Could not load user data.");
            loginButton.setDisable(true);
        }
    }

    @FXML
    private void handleShowPassword() {
        boolean show = showPasswordCheckBox.isSelected();

        // Toggle visibility
        visiblePasswordField.setVisible(show);
        visiblePasswordField.setManaged(show);
        passwordField.setVisible(!show);
        passwordField.setManaged(!show);
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        User user = userService.authenticate(username, password);

        if (user != null) {
            goToDashboard(user);
        } else {
            incorrectLabel.setVisible(true);
            loginCount += 1;

            if (loginCount >= 3) {
                incorrectLabel.setVisible(false);
                failedLabel.setVisible(true);
                loginButton.setDisable(true);
            }
        }
    }

    private void goToDashboard(User user) {
        String role = user.getRole();
        SceneUtils.setSideBarAndDashboard(holderPane,"/org/tcms/view/ToolbarView.fxml", role);
        SceneUtils.clearScreenColor(holderPane);
    }
}