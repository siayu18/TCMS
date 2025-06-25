package org.tcms.controller;

import javafx.scene.control.*;
import org.tcms.model.Student;
import org.tcms.service.StudentService;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.tcms.utils.Helper;

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
    public Label passwordErrorLabel;
    public Label usernameErrorLabel;

    private StudentService studentService;
    private String selectedAccountID;

    public void initialize() {
        studentService = new StudentService();

        accountIDColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getAccountId()));
        nameColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getUsername()));
        passwordColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getPassword()));

        loadStudentData();

        delBtn.setDisable(true);
        updateBtn.setDisable(true);
        saveBtn.setDisable(true);

        configureActions();
        configureListener();
    }

    private void configureActions() {
        updateBtn.setOnAction(e -> {
            saveBtn.setDisable(false);
            updateBtn.setDisable(true);
        });

        saveBtn.setOnAction(e -> {
            String newUsername = usernameField.getText().trim();
            String newPassword = passwordField.getText().trim();

            boolean isUsernameEmpty = Helper.validateNotEmpty(usernameField, usernameErrorLabel, "Username cannot be empty");
            boolean isPasswordEmpty = Helper.validateNotEmpty(passwordField, passwordErrorLabel, "Password cannot be empty");

            if (isUsernameEmpty) {
                usernameErrorLabel.setVisible(true);
                return;
            }

            if (isPasswordEmpty) {
                passwordErrorLabel.setVisible(true);
                return;
            }

            usernameErrorLabel.setVisible(false);
            passwordErrorLabel.setVisible(false);
            studentService.updateStudent(selectedAccountID, newUsername, newPassword);
            loadStudentData();
            clearFields();
        });

        delBtn.setOnAction(e -> {
            if (selectedAccountID != null) {
                studentService.deleteStudent(selectedAccountID);
                loadStudentData();
                clearFields();
            }
        });
    }

    private void configureListener() {
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
