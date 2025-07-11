package org.tcms.controller.TutorController;

import com.jfoenix.controls.JFXButton;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.tcms.model.TuitionClass;
import org.tcms.service.TuitionClassService;
import org.tcms.utils.AlertUtils;

import java.io.IOException;
import java.util.List;

public class UpdClassDetailsController {

    @FXML public TextField subjectField;
    @FXML public TextField informationField;
    @FXML public TextField chargesField;
    @FXML public TextField scheduleField;

    @FXML public TableView<TuitionClass> classTable;
    @FXML public TableColumn<TuitionClass, String> classIDColumn;
    @FXML public TableColumn<TuitionClass, String> subjectColumn;
    @FXML public TableColumn<TuitionClass, String> informationColumn;
    @FXML public TableColumn<TuitionClass, String> chargesColumn;
    @FXML public TableColumn<TuitionClass, String> dayColumn;
    @FXML public TableColumn<TuitionClass, String> startTimeColumn;
    @FXML public TableColumn<TuitionClass, String> endTimeColumn;
    @FXML public TableColumn<TuitionClass, String> levelColumn;

    @FXML public ComboBox <TuitionClass> levelBox;
    @FXML public JFXButton deleteBtn;
    @FXML public JFXButton updateBtn;

    private TuitionClassService tuitionClassService;
    private List <TuitionClass> tuitionClass;

    public void initialize(){
        try{
            tuitionClassService = new TuitionClassService();
        } catch (IOException e ){
            AlertUtils.showAlert("Data Loading Issue", "Failed to load data");
            return;
        }

        configureTable();
        loadClassData();

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
}
