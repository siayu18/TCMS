package org.tcms.controller.AdminController;

import com.jfoenix.controls.JFXButton;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.tcms.exception.EmptyFieldException;
import org.tcms.exception.ValidationException;
import org.tcms.model.Tutor;
import org.tcms.model.User;
import org.tcms.service.TutorService;
import org.tcms.service.UserService;
import org.tcms.utils.AlertUtils;
import org.tcms.utils.Helper;
import java.io.IOException;
import java.util.List;

public class ReassignTutorController {
    // FXML components
    @FXML private TextField subjectField;
    @FXML private JFXButton reassignBtn, deleteBtn, addSubjectBtn;
    @FXML private Label errorLabel;
    @FXML private ComboBox<Tutor> tutorName;
    @FXML private ComboBox<String> selectLevel;
    @FXML private TableView<Tutor> accountTable;
    @FXML private TableColumn<Tutor, String> tutorIdColumn, tutorNameColumn, assignedLevelColumn, assignedSubjectColumn;

    // Services & Variables
    private UserService userService;
    private TutorService tutorService;
    private String selectedTutorID;
    private String selectedSubject;
    private String selectedLevel;


    @FXML
    public void initialize() {
        try {
            userService = new UserService();
            tutorService = new TutorService();
        } catch (IOException e) {
            AlertUtils.showAlert("An error occurred while loading the data", "Failed to load data");
            return;
        }

        setTutorBox(tutorName);
        tutorName.setPromptText("Select Tutor");
        selectLevel.getItems().addAll("Level 1", "Level 2", "Level 3", "Level 4", "Level 5", "Level 6", "No Level");
        configureLevelComboBox(selectLevel);
        selectLevel.setPromptText("Select Level");
        configureTable();
        loadAccountData();
        loadTutorComboBox();
        configureActions();
    }

    private void setTutorBox(ComboBox<Tutor> box) {
        box.setCellFactory(cb ->
                new ListCell<>() {
                    @Override
                    protected void updateItem(Tutor tutor, boolean empty) {
                        super.updateItem(tutor, empty);
                        setText(empty || tutor == null ? null : tutor.getAccountId() + " - " + tutor.getUsername());
                    }
                });

        box.setButtonCell(new ListCell<>() {

            @Override
            protected void updateItem(Tutor item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getAccountId() + " - " + item.getUsername());
            }
        });
    }

    private void configureLevelComboBox(ComboBox<String> box) {
        box.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                // Set text to NULL when empty to show promptText
                setText(empty || item == null ? null : item);
            }
        });
    }


    private void configureActions() {

        addSubjectBtn.setOnAction(e -> {
            try {
                isRequiredEmpty();
                errorLabel.setVisible(false);
                AlertUtils.showInformation("Successfully Added New Subject And Tutor", tutorName.getValue().getUsername() + " will be teaching " + subjectField.getText());
                addTutorToSub();
                loadAccountData();
                clearAll();

            } catch (EmptyFieldException | ValidationException ex) {
                errorLabel.setText(ex.getMessage());
                errorLabel.setVisible(true);
            }
        });

        reassignBtn.setOnAction(e -> {
            try {
                // Get the currently selected tutor (to be replaced)
                Tutor oldTutor = accountTable.getSelectionModel().getSelectedItem();
                if (oldTutor == null) {
                    AlertUtils.showAlert("Error", "Please select a tutor assignment to reassign");
                    return;
                }

                // Get the newly selected tutor (to replace with)
                Tutor newTutor = tutorName.getValue();
                if (newTutor == null) {
                    AlertUtils.showAlert("Error", "Please select a new tutor");
                    return;
                }

                // Ensure we're not reassigning to the same tutor
                if (oldTutor.getAccountId().equals(newTutor.getAccountId())) {
                    AlertUtils.showAlert("Error", "New tutor cannot be the same as the current tutor");
                    return;
                }

                // Reassign in the service
                tutorService.reassignTutor(
                        oldTutor.getAccountId(),
                        newTutor.getAccountId(),
                        selectedSubject,
                        selectedLevel
                );

                // Refresh UI
                loadAccountData();
                clearAll();
                AlertUtils.showInformation("Success", "Tutor reassigned successfully");

            } catch (Exception ex) {
                errorLabel.setText("Failed to reassign tutor: " + ex.getMessage());
                errorLabel.setVisible(true);
            }
        });

        deleteBtn.setOnAction(e -> {
            if (selectedTutorID != null) {
                tutorService.deleteTutorAssignment(selectedTutorID, selectedSubject, selectedLevel);
                loadAccountData();
                clearAll();
            }
        });

        // Table selection listener
        accountTable.getSelectionModel().selectedItemProperty().addListener((obs, oldUser, selectedUser) -> {
            if (selectedUser != null) {

                subjectField.setText(selectedUser.getAssignedSubject());
                selectedTutorID = selectedUser.getAccountId();
                selectedSubject = selectedUser.getAssignedSubject();
                selectedLevel = selectedUser.getAssignedLevel();
                selectLevel.setValue(selectedUser.getAssignedLevel());
                tutorName.setValue(selectedUser);
                deleteBtn.setDisable(false);
                reassignBtn.setDisable(false);
                addSubjectBtn.setDisable(false);
            } else {
                clearAll();
            }
        });
    }

    private void isRequiredEmpty() throws EmptyFieldException {
        boolean hasEmptyFields = Helper.validateComboBoxNotEmpty(selectLevel) ||
                Helper.validateFieldNotEmpty(subjectField) ||
                Helper.validateComboBoxNotEmpty(tutorName);

        if (hasEmptyFields) {
            throw new EmptyFieldException("Required field(s) with indication (*) is empty!");
        }
    }

    private void loadTutorComboBox() {
        try {
            List<Tutor> tutors = tutorService.getAllTutorsFromAccount();

            tutorName.getItems().setAll(tutors);

        } catch (Exception e) {
            AlertUtils.showAlert("Error", "Failed to load tutors from account.csv");
        }
    }

    private void configureTable() {
        tutorIdColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getAccountId()));
        tutorNameColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getUsername()));
        assignedLevelColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getAssignedLevel()));
        assignedSubjectColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getAssignedSubject()));
    }

    private void loadAccountData() {
        try {
            List<Tutor> tutors = tutorService.getAllTutor();

            if (tutors.isEmpty()) {
                errorLabel.setText("No tutors found.");
                errorLabel.setVisible(true);
                return;
            }

            ObservableList<Tutor> tutorList = FXCollections.observableArrayList(tutors);
            accountTable.setItems(tutorList);

            errorLabel.setVisible(false);

        } catch (Exception e) {
            errorLabel.setText("Failed to load data: " + e.getMessage());
            errorLabel.setVisible(true);
            AlertUtils.showAlert("Table Error", "Could not load tutor data.");
        }
    }

    private void addTutorToSub(){
        String tutorID = tutorName.getValue().getAccountId();
        String assignedLevels = selectLevel.getValue();
        String assignedSubjects = subjectField.getText();

        User existingUser = userService.getUserByID(tutorID);
        if (existingUser == null) {
            AlertUtils.showAlert("Error", "Tutor user not found");
            return;
        }

        Tutor tutor = new Tutor(
                existingUser.getAccountId(),
                existingUser.getUsername(),
                existingUser.getPassword(),
                existingUser.getRole(),
                assignedLevels,
                assignedSubjects
        );
        tutorService.addTutorToSubject(tutor);
    }

    private void clearAll() {
        subjectField.clear();
        tutorName.setValue(null);
        selectLevel.setValue(null);
        errorLabel.setVisible(false);
    }
}
