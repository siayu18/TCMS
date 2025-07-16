package org.tcms.controller.TutorController;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.tcms.exception.EmptyFieldException;
import org.tcms.exception.ValidationException;
import org.tcms.model.TuitionClass;
import org.tcms.model.Tutor;
import org.tcms.service.TuitionClassService;
import org.tcms.utils.AlertUtils;
import org.tcms.utils.Helper;
import org.tcms.utils.Session;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CreateClassController {
    @FXML private TextField informationField;
    @FXML private TextField chargesField;
    @FXML private TextField startTimeField;
    @FXML private TextField endTimeField;
    @FXML private ComboBox<String> dayBox;
    @FXML private JFXComboBox<Tutor> chooseClassBox;
    @FXML private Label errorLabel;

    @FXML private TableView<TuitionClass> classTable;
    @FXML private TableColumn<TuitionClass, String> classIDColumn;
    @FXML private TableColumn<TuitionClass, String> subjectColumn;
    @FXML private TableColumn<TuitionClass, String> informationColumn;
    @FXML private TableColumn<TuitionClass, String> chargesColumn;
    @FXML private TableColumn<TuitionClass, String> dayColumn;
    @FXML private TableColumn<TuitionClass, String> startTimeColumn;
    @FXML private TableColumn<TuitionClass, String> endTimeColumn;
    @FXML private TableColumn<TuitionClass, String> levelColumn;
    @FXML private JFXButton submitBtn;
    @FXML private JFXButton clearBtn;

    private TuitionClassService tuitionClassService;
    private List<Tutor> assignedSubjects;

    public void initialize() {
        try {
            tuitionClassService = new TuitionClassService();
        } catch (IOException e) {
            AlertUtils.showAlert("Data Loading Issue", "Failed to load data");
            return;
        }

        assignedSubjects = tuitionClassService.getAssignedSubjectsForTutor();
        dayBox.getItems().addAll("Monday", "Tuesday", "Wednesday", "Thursday", "Friday");
        disabler();
        configureClassBox();
        configureTable();
        loadClassData();
        configureAction();

    }


    private void configureAction(){
        chooseClassBox.setOnAction(e -> {
            enabler();
        });

        submitBtn.setOnAction(e -> {
            try {
                isRequiredEmpty();
                isClassInfoValid(
                        chargesField.getText(),
                        startTimeField.getText(),
                        endTimeField.getText(),
                        dayBox.getValue()
                );

                tuitionClassService.addClass(
                        Session.getCurrentUserID(),
                        chooseClassBox.getValue().getAssignedSubject(),
                        informationField.getText(),
                        chargesField.getText(),
                        dayBox.getValue(),
                        startTimeField.getText(),
                        endTimeField.getText(),
                        chooseClassBox.getValue().getAssignedLevel()
                );

                errorLabel.setVisible(false);
                loadClassData();
                AlertUtils.showInformation("Successfully Created Class!",  "New class for "+ chooseClassBox.getValue().getAssignedLevel() +
                        "- " + chooseClassBox.getValue().getAssignedSubject() + " has been created!");
                clearFields();
                disabler();

            } catch (EmptyFieldException | ValidationException | IOException ex) {
                errorLabel.setText(ex.getMessage());
                errorLabel.setVisible(true);
            }
        });

        clearBtn.setOnAction( e -> {
            clearFields();
        });
    }


    private void configureTable(){
        classIDColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getClassID()));
        subjectColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getSubjectName()));
        informationColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getInformation()));
        chargesColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getCharges()));
        dayColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getDay()));
        startTimeColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getStartTime()));
        endTimeColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getEndTime()));
        levelColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getLevel()));
    }


    private void loadClassData() {
        ObservableList<TuitionClass> list = FXCollections.observableArrayList(
                tuitionClassService.getClassesFromTutor()
        );
        classTable.setItems(list);
    }


    private void configureClassBox () {
        chooseClassBox.getItems().setAll(assignedSubjects);

        chooseClassBox.setCellFactory(cb -> new ListCell<Tutor>() {
                    @Override
                    protected void updateItem(Tutor u, boolean empty) {
                        super.updateItem(u, empty);
                        setText(empty || u == null ? null : u.getAssignedLevel() + "- " + u.getAssignedSubject());
                    }
                }
        );

        chooseClassBox.setButtonCell(new ListCell<Tutor>() {
            @Override
            protected void updateItem(Tutor u, boolean empty) {
                super.updateItem(u, empty);
                setText(empty || u == null ? null : u.getAssignedLevel() + "- " + u.getAssignedSubject());
            }
        });
    }


    private void isRequiredEmpty() throws EmptyFieldException {
        if (Helper.validateComboBoxNotEmpty(chooseClassBox) ||
                Helper.validateFieldNotEmpty(informationField) ||
                Helper.validateFieldNotEmpty(chargesField) ||
                Helper.validateFieldNotEmpty(startTimeField) ||
                Helper.validateFieldNotEmpty(endTimeField) ||
                Helper.validateComboBoxNotEmpty(dayBox)) {
            throw new EmptyFieldException("Please ensure all fields are not empty!");
        }
    }


    private static boolean isTimeSlotOverlapping(String newDay, String newStartTime, String newEndTime) throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mma");
        TuitionClassService tuitionClassService = new TuitionClassService();

        LocalTime newStart = LocalTime.parse(newStartTime.toUpperCase(), formatter);
        LocalTime newEnd = LocalTime.parse(newEndTime.toUpperCase(), formatter);

        for (TuitionClass cls : tuitionClassService.getClassesFromTutor()) {
            if (!cls.getDay().equalsIgnoreCase(newDay)) continue;

            LocalTime existingStart = LocalTime.parse(cls.getStartTime().toUpperCase(), formatter);
            LocalTime existingEnd = LocalTime.parse(cls.getEndTime().toUpperCase(), formatter);

            boolean isOverlapping = newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart);

            if (isOverlapping) {
                return true;
            }
        }
        return false;
    }


    private static void isClassInfoValid(String charges, String startTime, String endTime, String day) throws ValidationException, IOException {
        if (!charges.matches("\\d+")) {
            throw new ValidationException("Charges must be an Integer Value!");
        }

        String timeRegex = "^(0?[1-9]|1[0-2]):[0-5][0-9](am|pm|AM|PM)$";

        if (!startTime.matches(timeRegex)) {
            throw new ValidationException("Start Time must be in format hh:mmam/pm (e.g., 9:00am)");
        }

        if (!endTime.matches(timeRegex)) {
            throw new ValidationException("End Time must be in format hh:mmam/pm (e.g., 10:00am)");
        }

        if (isTimeSlotOverlapping(day, startTime, endTime)) {
            throw new ValidationException("This time slot on " + day + " is already taken.");
        }
    }


    private void clearFields() {
        chooseClassBox.getSelectionModel().clearSelection();
        informationField.clear();
        chargesField.clear();
        dayBox.getSelectionModel().clearSelection();
        startTimeField.clear();
        endTimeField.clear();
    }


    private void disabler(){
        informationField.setDisable(true);
        chargesField.setDisable(true);
        dayBox.setDisable(true);
        startTimeField.setDisable(true);
        endTimeField.setDisable(true);
        clearBtn.setDisable(true);
        submitBtn.setDisable(true);
    }


    private void enabler(){
        informationField.setDisable(false);
        chargesField.setDisable(false);
        dayBox.setDisable(false);
        startTimeField.setDisable(false);
        endTimeField.setDisable(false);
        clearBtn.setDisable(false);
        submitBtn.setDisable(false);
    }
}
