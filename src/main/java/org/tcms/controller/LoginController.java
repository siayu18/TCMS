package org.tcms.controller;

import org.tcms.model.User;
import org.tcms.service.UserService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    private UserService userService;

    @FXML public Button loginButton;
    @FXML public ImageView tuitionBackground1;
    @FXML public ImageView tuitionBackground2;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField visiblePasswordField;
    @FXML private CheckBox showPasswordCheckBox;

    @FXML
    public void initialize() {
        visiblePasswordField.textProperty().bindBidirectional(passwordField.textProperty());

        try {
            userService = new UserService();
        } catch (IOException e) {
            showAlert("Error", "Could not load user data.");
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
            showAlert("Login Failed", "Invalid username or password.");
        }
    }

    private void goToDashboard(User user) {
        String role = user.getRole();
        String targetFile;

        switch (role) {
            case "Admin": targetFile = "AdminDashboardView.fxml"; break;
            case "Student": targetFile = "StudentDashboardView.fxml"; break;
            case "Tutor": targetFile = "TutorDashboardView.fxml"; break;
            case "Receptionist": targetFile = "ReceptionistDashboardView.fxml"; break;
            default:
                showAlert("Unknown Role", "Cannot load dashboard for role: " + role);
                return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/javfxproject/view/" + targetFile)
            );
            Parent root = loader.load();
            Stage stage = (Stage) loginButton.getScene().getWindow();
            double x = stage.getX();
            double y = stage.getY();

            Scene scene = new Scene(root, 700, 500);
            stage.setScene(scene);
            stage.setTitle(role + " Dashboard");
            stage.setX(x);
            stage.setY(y);
            stage.show();

        } catch (IOException e) {
            showAlert("Error", "Could not load " + role + " dashboard.");
        }
    }

    private void showAlert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.setHeaderText(null);
        a.setTitle(title);
        a.showAndWait();
    }

}