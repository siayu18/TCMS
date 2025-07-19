package org.tcms.controller.AdminController;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.tcms.exception.EmptyFieldException;
import org.tcms.exception.ValidationException;
import org.tcms.model.User;
import org.tcms.service.TutorService;
import org.tcms.service.UserService;
import org.tcms.utils.AlertUtils;
import org.tcms.utils.Helper;

import java.io.IOException;

public class UpdDelStaffController {
    // FXML components
    @FXML private TextField usernameField, passwordField;
    @FXML private JFXComboBox<String> selectRole;
    @FXML private Label errorLabel;
    @FXML private TableColumn<User, String> accountIDColumn, nameColumn, passwordColumn, roleColumn;
    @FXML private TableView<User> accountTable;
    @FXML private JFXButton deleteBtn, updateBtn;

    // Services & Variables
    private UserService userService;
    private String selectedAccountID;
    private TutorService tutorService;

    @FXML
    public void initialize() {
        try {
            userService = new UserService();
            tutorService = new TutorService();
        } catch (IOException e) {
            AlertUtils.showAlert("An error occurred while loading the data", "Failed to load data");
            return;
        }

        selectRole.getItems().addAll("Admin", "Tutor", "Receptionist");

        configureTable();
        loadAccountData();
        configureActions();
    }

    private void configureActions() {
        updateBtn.setOnAction(e -> {
            try {
                String newUsername = usernameField.getText().trim();
                String newPassword = passwordField.getText().trim();
                String newRole = selectRole.getValue();

                Helper.isUsernamePasswordEmpty(usernameField, passwordField);
                if (!Helper.validatePassword(passwordField.getText())) {
                    throw new ValidationException("Password should be more than 8 characters\nand contain at least 1 uppercase, lowercase, digit\nand special character.");
                }

                errorLabel.setVisible(false);
                userService.updateUser(selectedAccountID, newUsername, newPassword, newRole);
                loadAccountData();
                clearFields();
                AlertUtils.showInformation("Successfully Updated Student!", newUsername + "'s account has been updated!");

            } catch (EmptyFieldException | ValidationException ex) {
                errorLabel.setText(ex.getMessage());
                errorLabel.setVisible(true);
            }
        });

        deleteBtn.setOnAction(e -> {
            if (selectedAccountID != null) {
                try {
                    // Verify the user is a tutor (safety check)
                    User user = userService.getUserByID(selectedAccountID);
                    if (user == null || !"Tutor".equals(user.getRole())) {
                        AlertUtils.showAlert("Error", "Selected user is not a tutor.");
                        return;
                    }


                    // Replace tutor ID with "NO TUTOR" in tutor.csv
                    tutorService.markTutorAsDeletedInTutorCSV(selectedAccountID);

                    // Replace tutor ID with "NO TUTOR" in tuitionclass.csv
                    tutorService.markTutorAsDeletedInClassesCSV(selectedAccountID);

                    // Delete the tutor from account.csv
                    userService.deleteUser(selectedAccountID);

                    // Refresh UI
                    loadAccountData();
                    clearFields();
                    AlertUtils.showInformation("Success", "Tutor deleted, and records updated.");

                } catch (Exception ex) {
                    errorLabel.setText("Deletion failed: " + ex.getMessage());
                    errorLabel.setVisible(true);
                }
            }
        });

        // Table selection listener
        accountTable.getSelectionModel().selectedItemProperty().addListener((obs, oldUser, selectedUser) -> {
            if (selectedUser != null) {

                usernameField.setText(selectedUser.getUsername());
                passwordField.setText(selectedUser.getPassword());
                selectedAccountID = selectedUser.getAccountId();

                String role = selectedUser.getRole();
                selectRole.setValue(role);

                deleteBtn.setDisable(false);
                updateBtn.setDisable(false);
            } else {
                clearFields();
            }
        });
    }

    private void configureTable() {
        accountIDColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getAccountId()));
        nameColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getUsername()));
        passwordColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getPassword()));
        roleColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getRole()));
    }

    private void loadAccountData() {
        ObservableList<User> list = FXCollections.observableArrayList(userService.getAllUsers());
        accountTable.setItems(list);
    }

    private void clearFields() {
        usernameField.clear();
        passwordField.clear();
        selectRole.setValue(null);
        deleteBtn.setDisable(true);
        updateBtn.setDisable(true);
        selectedAccountID = null;
    }
}
