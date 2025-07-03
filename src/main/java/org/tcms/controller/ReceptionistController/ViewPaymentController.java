package org.tcms.controller.ReceptionistController;

import com.jfoenix.controls.JFXComboBox;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

public class ViewPaymentController {
    public JFXComboBox chooseStudentBox;
    public AnchorPane viewPane;
    public Label title;
    public TableView<StudentPayment> paymentTable;
    public TableColumn<StudentPayment, String> accountIDColumn;
    public TableColumn<StudentPayment, String> nameColumn;
    public TableColumn<StudentPayment, String> classIDColumn;
    public TableColumn<StudentPayment, String> amountColumn;
    public TableColumn<StudentPayment, String> subjectNameColumn;
    public TableColumn<StudentPayment, String> timeColumn;
    public TableColumn<StudentPayment, String> dateColumn;

    private List<Student> students;
    private List<Payment> payments;
    private Map<String, TuitionClass> classMap;
    private Map<String, Enrollment> enrollmentMap;
    private Student selectedStudent;

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
        payments = paymentService.getAcceptedPayments();
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

        configureTable();
        ComponentUtils.configureStudentBox(chooseStudentBox, students);
        configureActions();
    }

    private void configureActions() {
        chooseStudentBox.setOnAction(e -> {
            selectedStudent = ((Student) chooseStudentBox.getValue());
            if (selectedStudent != null) {
                viewPane.setVisible(true);
                loadPaymentTable(selectedStudent);
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
        dateColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getDate()));
        timeColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getTime()));
    }

    private void loadPaymentTable(Student student) {
        ObservableList<StudentPayment> viewList = FXCollections.observableArrayList(MappingUtils.mapPaymentsForStudent(student, payments, enrollmentMap, classMap));
        paymentTable.setItems(viewList);
    }
}
