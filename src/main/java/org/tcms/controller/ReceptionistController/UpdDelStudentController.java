package org.tcms.controller.ReceptionistController;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.tcms.exception.EmptyFieldException;
import org.tcms.exception.ValidationException;
import org.tcms.model.Student;
import org.tcms.service.StudentService;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.tcms.utils.AlertUtils;
import org.tcms.utils.Helper;

import java.io.IOException;

public class UpdDelStudentController {
    public TextField usernameField;
    public TextField passwordField;
    public Button delBtn;
    public Button saveBtn;
    public Button updateBtn;
    public TableView<Student> studentTable;
    public TableColumn<Student, String> accountIDColumn;
    public TableColumn<Student, String> nameColumn;
    public TableColumn<Student, String> passwordColumn;
    public Label errorLabel;

    private StudentService studentService;
    private String selectedAccountID;

    @FXML
    public void initialize() {
        try {
            studentService = new StudentService();
        } catch (IOException e) {
            AlertUtils.showAlert("Data Loading Issue", "Failed to load data");
            return;
        }

        configureTable();
        loadStudentData();

        delBtn.setDisable(true);
        updateBtn.setDisable(true);
        saveBtn.setDisable(true);

        configureActions();
    }

    private void configureActions() {
        updateBtn.setOnAction(e -> {
            AlertUtils.showInformation("Update Student", "Please select a 'Save' button to update.");
            saveBtn.setDisable(false);
            updateBtn.setDisable(true);
        });

        saveBtn.setOnAction(e -> {
            try {
                String newUsername = usernameField.getText().trim();
                String newPassword = passwordField.getText().trim();

                Helper.isUsernamePasswordEmpty(usernameField, passwordField, errorLabel);
                if (Helper.validatePassword(passwordField.getText())) {
                    throw new ValidationException("Password should be more than 8 characters\nand contain at least 1 uppercase, lowercase, digit\nand special character.");
                }

                // everything ok, then proceed
                errorLabel.setVisible(false);
                studentService.updateUser(selectedAccountID, newUsername, newPassword);
                loadStudentData();
                clearFields();
                AlertUtils.showInformation("Successfully Updated Student!", usernameField.getText() + "'s account has been updated!");

            } catch (EmptyFieldException | ValidationException ex) {
                errorLabel.setText(ex.getMessage());
                errorLabel.setVisible(true);
            }
        });

        delBtn.setOnAction(e -> {
            if (selectedAccountID != null) {
                studentService.deleteUser(selectedAccountID);
                loadStudentData();
                clearFields();
            }
        });

        studentTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, sel) -> {
            if (sel != null) {
                Student student = sel;
                usernameField.setText(student.getUsername());
                passwordField.setText(student.getPassword());
                selectedAccountID = student.getAccountId();
                delBtn.setDisable(false);
                updateBtn.setDisable(false);
                saveBtn.setDisable(true);
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
    }

    private void loadStudentData() {
        ObservableList<Student> list = FXCollections.observableArrayList(
                studentService.getAllStudents()
        );
        studentTable.setItems(list);
    }

    private void clearFields() {
        usernameField.clear();
        passwordField.clear();
        delBtn.setDisable(true);
        updateBtn.setDisable(true);
        saveBtn.setDisable(true);
        selectedAccountID = null;
    }

}
