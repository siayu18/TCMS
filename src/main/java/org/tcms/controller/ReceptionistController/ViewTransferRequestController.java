package org.tcms.controller.ReceptionistController;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.tcms.model.*;
import org.tcms.service.*;
import org.tcms.utils.AlertUtils;
import org.tcms.utils.MappingUtils;
import org.tcms.utils.Session;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ViewTransferRequestController {

    public TableView<StudentRequestEntry> requestTable;
    public TableColumn<StudentRequestEntry, String> studentIDColumn;
    public TableColumn<StudentRequestEntry, String> studentNameColumn;
    public TableColumn<StudentRequestEntry, String> oldClassIDColumn;
    public TableColumn<StudentRequestEntry, String> oldSubjectNameColumn;
    public TableColumn<StudentRequestEntry, String> newClassIDColumn;
    public TableColumn<StudentRequestEntry, String> newSubjectNameColumn;
    public TableColumn<StudentRequestEntry, String> reasonColumn;
    public Button approveBtn;
    public Button rejectBtn;

    private List<Request> requests;
    private Map<String, TuitionClass> classMap;
    private Map<String, Student> studentMap;
    private StudentRequestEntry selectedRequest;
    private String rejectMessage = "Sorry, your request to transfer for %s-%s to %s-%s has been rejected.";
    private String approveMessage = "Your request to transfer for %s-%s to %s-%s has been accepted.";

    private RequestService requestService;
    private TuitionClassService tuitionClassService;
    private StudentService studentService;
    private CommunicationService comService;
    private EnrollmentService enrollmentService;

    public void initialize() {
        try {
            requestService = new RequestService();
            studentService = new StudentService();
            tuitionClassService = new TuitionClassService();
            comService = new CommunicationService();
            enrollmentService = new EnrollmentService();
        } catch (IOException e) {
            AlertUtils.showAlert("Data Loading Issue", "Failed to load data");
            return;
        }

        requests = requestService.getAllRequest();

        // Make it into {id:object of id} to make it easier for mapping
        studentMap = studentService.getAllStudents().stream()
                .collect(Collectors.toMap(
                        Student::getAccountId,
                        Student -> Student
                ));
        classMap = tuitionClassService.getAllClasses().stream()
                .collect(Collectors.toMap(
                        TuitionClass::getClassID,
                        TuitionClass -> TuitionClass
                ));

        approveBtn.setDisable(true);
        rejectBtn.setDisable(true);
        configureTable();
        loadRequestTable();
        configureActions();
    }

    private void configureActions() {
        approveBtn.setOnAction(e -> {
            // Transfer Enrollment & Delete Request (Indicating it is done)
            enrollmentService.transferEnrollment(
                    selectedRequest.getStudentID(),
                    selectedRequest.getOldClassID(),
                    selectedRequest.getNewClassID()
            );
            requestService.deleteRequest(selectedRequest.getRequestID());

            // Send Approval Message (Communication Hub)
            comService.sendMessage(
                    Session.getCurrentUserID(),
                    selectedRequest.getStudentID(),
                    String.format(
                            approveMessage,
                            selectedRequest.getOldClassID(),
                            selectedRequest.getOldSubjectName(),
                            selectedRequest.getNewClassID(),
                            selectedRequest.getNewSubjectName())
            );
            AlertUtils.showInformation("Request Approved",
                    "Enrollment of the selected student has been changed and approval message is sent to the student (Communication Hub)");

            requests = requestService.getAllRequest(); // refresh the data before loading to table
            loadRequestTable();
            requestTable.getSelectionModel().clearSelection();
            approveBtn.setDisable(true);
            rejectBtn.setDisable(true);
        });

        rejectBtn.setOnAction(e -> {
            // Delete Request (Indicating it is done)
            requestService.deleteRequest(selectedRequest.getRequestID());

            // Send Rejection Message (Communication Hub)
            comService.sendMessage(
                    Session.getCurrentUserID(),
                    selectedRequest.getStudentID(),
                    String.format(
                            rejectMessage,
                            selectedRequest.getOldClassID(),
                            selectedRequest.getOldSubjectName(),
                            selectedRequest.getNewClassID(),
                            selectedRequest.getNewSubjectName())
            );
            AlertUtils.showInformation("Request Rejected", "Rejection message is sent to the student (Communication Hub)");

            requests = requestService.getAllRequest(); // refresh the data before loading to table
            loadRequestTable();
            requestTable.getSelectionModel().clearSelection();
            approveBtn.setDisable(true);
            rejectBtn.setDisable(true);
        });

        requestTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, sel) -> {
            if (sel != null) {
                StudentRequestEntry studentRequest = sel;
                approveBtn.setDisable(false);
                rejectBtn.setDisable(false);
                selectedRequest = studentRequest;
            }
        });
    }

    private void configureTable() {
        studentIDColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getStudentID()));
        studentNameColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getStudentName()));
        oldClassIDColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getOldClassID()));
        oldSubjectNameColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getOldSubjectName()));
        newClassIDColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getNewClassID()));
        newSubjectNameColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getNewSubjectName()));
        reasonColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getReason()));
    }

    private void loadRequestTable() {
        ObservableList<StudentRequestEntry> viewList = FXCollections.observableArrayList(
                MappingUtils.mapRequestsForStudent(requests, studentMap, classMap));
        requestTable.setItems(viewList);
    }
}
