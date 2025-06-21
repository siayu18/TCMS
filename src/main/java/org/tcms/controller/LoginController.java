package org.tcms.controller;

import org.tcms.utils.AlertUtils;
import org.tcms.utils.SceneUtils;
import org.tcms.model.User;
import org.tcms.service.UserService;
// JavaFX Libraries
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
// Java Libraries
import java.io.IOException;

public class LoginController {
    // Variables/Constants
    private UserService userService;
    private static final int loginCountMax = 3;
//    private static final int cooldownTimer = 30;
    private Timeline cooldownTimeline;
    private int loginCount = 0;

    // FXML Variables/Constants
    @FXML public AnchorPane holderPane;
    @FXML public Label incorrectLabel, cooldownLabel;
    @FXML public Button loginButton;
    @FXML private TextField usernameField, visiblePasswordField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox showPasswordCheckBox;

    @FXML
    public void initialize() {
        visiblePasswordField.textProperty().bindBidirectional(passwordField.textProperty());

        try {
            userService = new UserService();
            loginButton.setDefaultButton(true);
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

        // Check for empty fields
        if (username.isEmpty() || password.isEmpty()) {
            incorrectLabel.setText("Username or password cannot be empty.");
            incorrectLabel.setVisible(true);
            return;
        }

        User user = userService.authenticate(username, password);

        if (user != null) {
            goToDashboard(user);
        } else {
            incorrectLabel.setVisible(true);
            loginCount += 1;
            incorrectLabel.setText("Incorrect username or password.\n Login attempts remaining: " + (loginCountMax - loginCount));

            if (loginCount >= loginCountMax) {
                incorrectLabel.setVisible(false);
                loginButton.setDisable(true);
                startCooldownTimer();
            }
        }
    }

    private void goToDashboard(User user) {
        String role = user.getRole();
        SceneUtils.setSideBarAndDashboard(holderPane,"/org/tcms/view/ToolbarView.fxml", role);
        SceneUtils.clearScreenColor(holderPane);
    }

    private void startCooldownTimer() {
        final int[] secondsLeft = { 5 }; // countdown starting value

        cooldownLabel.setVisible(true);
        loginButton.setDisable(true);

        cooldownLabel.setText("Please wait " + secondsLeft[0] + " seconds before trying again.");

        cooldownTimeline = new Timeline(
                new KeyFrame(Duration.seconds(1), event -> {
                    secondsLeft[0]--;
                    if (secondsLeft[0] > 0) {
                        cooldownLabel.setText("Please wait " + secondsLeft[0] + " seconds before trying again.");
                    } else {
                        cooldownTimeline.stop();
                        loginCount = 0;
                        cooldownLabel.setVisible(false);
                        loginButton.setDisable(false);
                    }
                })
        );

        cooldownTimeline.setCycleCount(5);
        cooldownTimeline.play();
    }
}