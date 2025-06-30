package org.tcms.controller;

import org.tcms.navigation.Role;
import org.tcms.navigation.View;
import org.tcms.utils.AlertUtils;
import org.tcms.utils.Helper;
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
import org.tcms.utils.Session;
// Java Libraries
import java.io.IOException;

public class LoginController {
    // Variables/Constants
    private UserService userService;
    private static final int loginCountMax = 3;
    private Timeline cooldownTimeline;
    private static final int cooldownTime = 3;
    private int loginCount = 0;

    // FXML Variables/Constants
    @FXML public AnchorPane mainPane;
    @FXML public Label incorrectLabel, cooldownLabel;
    @FXML public Button loginButton;
    @FXML private TextField usernameField, visiblePasswordField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox showPasswordCheckBox;

    @FXML
    public void initialize() {
        visiblePasswordField.textProperty().bindBidirectional(passwordField.textProperty());
        loginButton.setOnAction(e -> handleLogin());
        showPasswordCheckBox.setOnAction(e -> handleShowPassword());

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

        boolean isUsernameEmpty = Helper.validateFieldNotEmpty(usernameField, incorrectLabel, "Username or Password cannot be empty");
        boolean isPasswordEmpty = Helper.validateFieldNotEmpty(passwordField, incorrectLabel, "Username or Password cannot be empty");

        // Check for empty fields
        if (isUsernameEmpty || isPasswordEmpty) {
            incorrectLabel.setVisible(true);
            return;
        }

        User user = userService.authenticate(username, password);

        if (user != null) {
            // Set current user
            Session.setCurrentUser(user);

            // Convert the user role to your enum
            Role role = Role.fromString(user.getRole());

            // Load toolbarView.fxml into the login's mainPane (AnchorPane)
            // return the controller to the object created, so we can control and initialize the scene
            ToolbarController tbController = SceneUtils.setContent(mainPane, View.TOOLBAR);

            // initialize side menu and dashboard
            tbController.initializeWith(role);

            // clear background colour
            SceneUtils.clearScreenColor(mainPane);
        } else {
            incorrectLabel.setVisible(true);
            loginCount ++;
            incorrectLabel.setText("Incorrect username or password.\n Login attempts remaining: " + (loginCountMax - loginCount));

            if (loginCount >= loginCountMax) {
                incorrectLabel.setVisible(false);
                loginButton.setDisable(true);
                startCooldownTimer();
            }
        }
    }

    private void showOnly(Label labelToShow) {
        // Hide all labels first
        incorrectLabel.setVisible(false);
        cooldownLabel.setVisible(false);
        // Show the requested label
        labelToShow.setVisible(true);
    }

    private void startCooldownTimer() {
        final int[] secondsLeft = { cooldownTime }; // countdown starting value
        showOnly(cooldownLabel);

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
        cooldownTimeline.setCycleCount(cooldownTime);
        cooldownTimeline.play();
    }
}
