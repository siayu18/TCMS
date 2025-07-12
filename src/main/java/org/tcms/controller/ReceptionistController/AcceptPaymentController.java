package org.tcms.controller.ReceptionistController;

import com.jfoenix.controls.JFXComboBox;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import org.tcms.model.*;
import org.tcms.service.EnrollmentService;
import org.tcms.service.PaymentService;
import org.tcms.service.StudentService;
import org.tcms.service.TuitionClassService;
import org.tcms.utils.AlertUtils;
import org.tcms.utils.ComponentUtils;
import org.tcms.utils.MappingUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AcceptPaymentController {
    @FXML private AnchorPane paymentPane;
    @FXML private TableView<StudentPayment> paymentTable;
    @FXML private TableColumn<StudentPayment, String> accountIDColumn;
    @FXML private TableColumn<StudentPayment, String> nameColumn;
    @FXML private TableColumn<StudentPayment, String> classIDColumn;
    @FXML private TableColumn<StudentPayment, String> subjectNameColumn;
    @FXML private TableColumn<StudentPayment, String> amountColumn;
    @FXML private JFXComboBox<Student> chooseStudentBox;
    @FXML private Button acceptBtn;

    private List<Student> students;
    private List<Payment> payments;
    private Map<String, TuitionClass> classMap;
    private Map<String, Enrollment> enrollmentMap;
    private Student selectedStudent;
    private String selectedPaymentID;

    private StudentService studentService;
    private TuitionClassService tuitionClassService;
    private EnrollmentService enrollmentService;
    private PaymentService paymentService;

    public void initialize() {
        try {
            studentService = new StudentService();
            tuitionClassService = new TuitionClassService();
            enrollmentService = new EnrollmentService();
            paymentService = new PaymentService();
        } catch (IOException e) {
            AlertUtils.showAlert("Data Loading Issue", "Failed to load data");
            return;
        }

        students = studentService.getAllStudents();
        payments = paymentService.getUnacceptedPayments();
        classMap = tuitionClassService.getAllClasses().stream()
                .collect(Collectors.toMap(
                        TuitionClass::getClassID,
                        tuitionClass -> tuitionClass
                ));
        enrollmentMap = enrollmentService.getAllEnrollment().stream()
                        .collect(Collectors.toMap(
                                Enrollment::getEnrollmentID,
                                enrollment -> enrollment
                        ));

        acceptBtn.setDisable(true);
        configureTable();
        ComponentUtils.configureStudentBox(chooseStudentBox, students);
        configureActions();
    }

    private void configureActions() {
        chooseStudentBox.setOnAction(e -> {
            selectedStudent = ((Student) chooseStudentBox.getValue());
            if (selectedStudent != null) {
                paymentPane.setVisible(true);
                acceptBtn.setVisible(true);
                loadPaymentTable(selectedStudent);
            }
        });

        acceptBtn.setOnAction(e -> {
            AlertUtils.showInformation("Successfully Accepted Payment!", "Payment is accepted");
            paymentService.updatePaymentStatus(selectedPaymentID); // update student's payment status
            payments = paymentService.getUnacceptedPayments(); // refresh the data before loading to table
            loadPaymentTable(selectedStudent);
            paymentTable.getSelectionModel().clearSelection();
            acceptBtn.setDisable(true);
        });

        paymentTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, sel) -> {
            if (sel != null) {
                StudentPayment studentPayment = sel;
                acceptBtn.setDisable(false);
                selectedPaymentID = studentPayment.getPaymentID();
            }
        });
    }

    private void configureTable() {
        accountIDColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getStudentID()));
        nameColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getStudentName()));
        classIDColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getClassID()));
        subjectNameColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getSubjectName()));
        amountColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getAmount()));
    }

    private void loadPaymentTable(Student student) {
        ObservableList<StudentPayment> viewList = FXCollections.observableArrayList(MappingUtils.mapPaymentsForStudent(student, payments, enrollmentMap, classMap));
        paymentTable.setItems(viewList);
    }

}
