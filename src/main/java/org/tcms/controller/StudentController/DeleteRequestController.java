package org.tcms.controller.StudentController;

import com.jfoenix.controls.JFXButton;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.tcms.model.Request;
import org.tcms.model.TuitionClass;
import org.tcms.service.*;
import org.tcms.utils.AlertUtils;
import org.tcms.utils.Session;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class DeleteRequestController {
    //FXML Components
    @FXML private TableView<Request> requestTable;
    @FXML private TableColumn<Request, String> requestIDColumn, oldSubjectColumn,newSubjectColumn, reasonColumn;
    @FXML private JFXButton deleteBtn;

    //Services
    private RequestService requestService;
    private TuitionClassService tuitionClassService;
    private String currentStudentId;

    @FXML
    public void initialize() {
        try {
            // Initialize services
            requestService = new RequestService();
            tuitionClassService = new TuitionClassService();

            // Get current student ID from session
            currentStudentId = Session.getCurrentUserID();

            // Validate logged-in student
            if (currentStudentId == null) {
                AlertUtils.showAlert("Error", "No student logged in. Please log in first.");
                disableUI();
                return;
            }

            loadStudentRequests();
            configureTable();

            // Enable delete button only when a request is selected
            requestTable.getSelectionModel().selectedItemProperty().addListener(
                    (obs, oldVal, newVal) -> deleteBtn.setDisable(newVal == null)
            );

            deleteBtn.setOnAction(e -> deleteSelectedRequest());

        } catch (IOException e) {
            AlertUtils.showAlert("Error", "Failed to load data: " + e.getMessage());
            disableUI();
        }
    }

    private void loadStudentRequests() {
        List<Request> allRequests = requestService.getAllRequest();

        // Filter requests for the current student
        List<Request> studentRequests = allRequests.stream()
                .filter(request -> currentStudentId.equals(request.getStudentID()))
                .collect(Collectors.toList());

        // Populate table
        requestTable.setItems(FXCollections.observableArrayList(studentRequests));

        if (studentRequests.isEmpty()) {
            AlertUtils.showInformation("No Requests", "You have no pending transfer requests.");
        }
    }

    private void configureTable() {
        // Request ID column
        requestIDColumn.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getRequestID())
        );

        // Old Subject column (convert OldClassID to subject name)
        oldSubjectColumn.setCellValueFactory(cellData -> {
            String oldClassId = cellData.getValue().getOldClassID();
            TuitionClass oldClass = tuitionClassService.getClassByID(oldClassId);
            String oldSubject = (oldClass != null) ? oldClass.getSubjectName() : "Unknown";
            return new ReadOnlyStringWrapper(oldSubject);
        });

        // New Subject column (convert NewClassID to subject name)
        newSubjectColumn.setCellValueFactory(cellData -> {
            String newClassId = cellData.getValue().getNewClassID();
            TuitionClass newClass = tuitionClassService.getClassByID(newClassId);
            String newSubject = (newClass != null) ? newClass.getSubjectName() : "Unknown";
            return new ReadOnlyStringWrapper(newSubject);
        });

        // Reason column
        reasonColumn.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getReason())
        );
    }

    private void deleteSelectedRequest() {
        Request selectedRequest = requestTable.getSelectionModel().getSelectedItem();

        if (selectedRequest == null) {
            AlertUtils.showAlert("Error", "Please select a request to delete.");
            return;
        }

        requestService.deleteRequest(selectedRequest.getRequestID());
        loadStudentRequests();

        AlertUtils.showInformation("Deleted", "Request has been deleted successfully.");
    }

    private void disableUI() {
        requestTable.setDisable(true);
        deleteBtn.setDisable(true);
    }
}
