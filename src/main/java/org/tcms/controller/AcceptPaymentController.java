package org.tcms.controller;

import com.jfoenix.controls.JFXComboBox;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import org.tcms.model.StudentPayment;
import org.tcms.model.Payment;
import org.tcms.model.Student;
import org.tcms.model.TuitionClass;
import org.tcms.service.PaymentService;
import org.tcms.service.StudentService;
import org.tcms.service.TuitionClassService;
import org.tcms.utils.ComponentUtils;
import org.tcms.utils.MappingUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AcceptPaymentController {
    public AnchorPane paymentPane;
    public TableView<StudentPayment> paymentTable;
    public TableColumn<StudentPayment, String> paymentIDColumn;
    public TableColumn<StudentPayment, String> accountIDColumn;
    public TableColumn<StudentPayment, String> nameColumn;
    public TableColumn<StudentPayment, String> classIDColumn;
    public TableColumn<StudentPayment, String> subjectNameColumn;
    public TableColumn<StudentPayment, String> amountColumn;
    public JFXComboBox<Student> chooseStudentBox;
    public Button acceptBtn;
    public Label errorLabel;

    private List<Student> students;
    private List<Payment> payments;
    private Map<String, TuitionClass> classMap;
    private Student selectedStudent;
    private String selectedPaymentID;
    private StudentService studentService;
    private TuitionClassService tuitionClassService;
    private PaymentService paymentService;

    public void initialize() {
        try {
            studentService = new StudentService();
            tuitionClassService = new TuitionClassService();
            paymentService = new PaymentService();
        } catch (IOException e) {
            errorLabel.setText("Failed to load data.");
            errorLabel.setVisible(true);
            return;
        }

        students = studentService.getAllStudents();
        payments = paymentService.getUnacceptedPayments();
        classMap = tuitionClassService.getAllClasses().stream()
                .collect(Collectors.toMap(
                        TuitionClass::getClassID,
                        tuitionClass -> tuitionClass
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
            paymentService.updatePaymentStatus(selectedPaymentID);
            payments = paymentService.getUnacceptedPayments();
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
        paymentIDColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getPaymentID()));
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
        ObservableList<StudentPayment> viewList = FXCollections.observableArrayList(MappingUtils.mapPaymentsForStudent(student, payments, classMap));
        paymentTable.setItems(viewList);
    }

}
