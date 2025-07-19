package org.tcms.controller.TutorController;

import com.jfoenix.controls.JFXButton;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.tcms.exception.EmptyFieldException;
import org.tcms.exception.ValidationException;
import org.tcms.model.TuitionClass;
import org.tcms.service.TuitionClassService;
import org.tcms.utils.AlertUtils;
import org.tcms.utils.Helper;
import java.io.IOException;

public class UpdClassDetailsController {
    @FXML private TextField informationField;
    @FXML private TextField chargesField;
    @FXML private TextField startTimeField;
    @FXML private TextField endTimeField;
    @FXML private ComboBox<String> dayBox;
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
    @FXML private JFXButton deleteBtn;
    @FXML private JFXButton updateBtn;

    private TuitionClassService tuitionClassService;

    public void initialize(){
        try{
            tuitionClassService = new TuitionClassService();
        } catch (IOException e ){
            AlertUtils.showAlert("Data Loading Issue", "Failed to load data");
            return;
        }

        dayBox.getItems().addAll("Monday", "Tuesday", "Wednesday", "Thursday", "Friday");
        disabler();
        configureTable();
        loadClassData();
        configureAction();

    }


    private void configureAction(){
        classTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, tuitionClass) -> {
            if (tuitionClass != null) {
                informationField.setText(tuitionClass.getInformation());
                chargesField.setText(tuitionClass.getCharges());
                startTimeField.setText(tuitionClass.getStartTime());
                endTimeField.setText(tuitionClass.getEndTime());
                dayBox.setValue(tuitionClass.getDay());
                errorLabel.setVisible(false);
                enabler();
            }
        });

        updateBtn.setOnAction(e -> {
            try {
                isRequiredEmpty();
                TuitionClass selectedClass = classTable.getSelectionModel().getSelectedItem();
                Helper.isClassInfoValid(
                        selectedClass.getClassID(),
                        chargesField.getText(),
                        startTimeField.getText(),
                        endTimeField.getText(),
                        dayBox.getValue()
                );


                tuitionClassService.updateClass(
                        selectedClass.getClassID(),
                        informationField.getText(),
                        chargesField.getText(),
                        dayBox.getValue(),
                        startTimeField.getText(),
                        endTimeField.getText()
                );

                errorLabel.setVisible(false);
                loadClassData();
                clearFields();
                disabler();
                AlertUtils.showInformation("Successfully Updated Class!", selectedClass.getSubjectName() + "'s details has been updated!");

            } catch (EmptyFieldException | ValidationException | IOException ex) {
                errorLabel.setText(ex.getMessage());
                errorLabel.setVisible(true);
            }
        });

        deleteBtn.setOnAction(e -> {
            try {
                isRequiredEmpty();
                TuitionClass selectedClass = classTable.getSelectionModel().getSelectedItem();
                if (selectedClass != null) {
                    tuitionClassService.deleteClass(selectedClass.getClassID());
                    errorLabel.setVisible(false);
                    loadClassData();
                    clearFields();
                    disabler();
                    AlertUtils.showInformation("Successfully Deleted Class!", selectedClass.getSubjectName() + "class on" + selectedClass.getDay() + "has been deleted!");
                }

            } catch (EmptyFieldException | ValidationException ex) {
                errorLabel.setText(ex.getMessage());
                errorLabel.setVisible(true);
            }
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


    private void isRequiredEmpty() throws EmptyFieldException {
        if (Helper.validateFieldNotEmpty(informationField) ||
                Helper.validateFieldNotEmpty(chargesField) ||
                Helper.validateFieldNotEmpty(startTimeField) ||
                Helper.validateFieldNotEmpty(endTimeField) ||
                Helper.validateComboBoxNotEmpty(dayBox)) {
            throw new EmptyFieldException("Please ensure all fields are not empty!");
        }
    }

    private void clearFields() {
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
        deleteBtn.setDisable(true);
        updateBtn.setDisable(true);
    }

    private void enabler(){
        informationField.setDisable(false);
        chargesField.setDisable(false);
        dayBox.setDisable(false);
        startTimeField.setDisable(false);
        endTimeField.setDisable(false);
        deleteBtn.setDisable(false);
        updateBtn.setDisable(false);
    }
}
