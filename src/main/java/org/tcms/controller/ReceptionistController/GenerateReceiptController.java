package org.tcms.controller.ReceptionistController;

import com.jfoenix.controls.JFXComboBox;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.tcms.model.*;
import org.tcms.service.*;
import org.tcms.utils.AlertUtils;
import org.tcms.utils.ComponentUtils;
import org.tcms.utils.MappingUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GenerateReceiptController {

    @FXML private Button generateBtn;
    @FXML private Label errorLabel;
    @FXML private AnchorPane receiptPane;
    @FXML private VBox receiptBox;
    @FXML private JFXComboBox chooseStudentBox;
    @FXML private GridPane paymentGrid;
    @FXML private Label studentLabel;
    @FXML private Label dateLabel;
    @FXML private Label totalLabel;

    private List<Student> students;
    private List<Payment> payments;
    private Map<String, TuitionClass> classMap;
    private Map<String, Enrollment> enrollmentMap;
    private Student selectedStudent;

    private StudentService studentService;
    private TuitionClassService tuitionClassService;
    private EnrollmentService enrollmentService;
    private PaymentService paymentService;
    private ReceiptService receiptService;

    @FXML
    public void initialize() {
        try {
            studentService = new StudentService();
            tuitionClassService = new TuitionClassService();
            enrollmentService = new EnrollmentService();
            paymentService = new PaymentService();
            receiptService = new ReceiptService();
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

        ComponentUtils.configureStudentBox(chooseStudentBox, students);
        configureActions();
    }

    private void configureActions() {
        chooseStudentBox.setOnAction(e -> {
            selectedStudent = ((Student) chooseStudentBox.getValue());
            if (selectedStudent != null) {
                receiptPane.setVisible(true);
                generateBtn.setVisible(true);
            }
        });

        generateBtn.setOnAction(e -> {
            receiptBox.setVisible(true);
            if (receiptService.isReceiptGenerated(selectedStudent)) {
                errorLabel.setText("Receipt is generated before, loading the receipt...");
                errorLabel.setVisible(true);
            } else {
                errorLabel.setVisible(false);
                receiptService.addReceipt(selectedStudent);
            }

            renderReceipt();
        });
    }

    private void renderReceipt() {
        receiptBox.setPadding(new Insets(20));
        receiptBox.setSpacing(15);

        // Set Header
        String currentDate = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy"));
        dateLabel.setText("Date: " + currentDate);
        studentLabel.setText("Student: " + selectedStudent.getUsername());

        // Add Data Rows
        clearGridRows(paymentGrid, 1); // Clear rows every render to avoid old data leaving there when a new student is selected
        List<StudentPayment> studentPayments = MappingUtils.mapPaymentsForStudent(selectedStudent, payments, enrollmentMap, classMap);
        for (int i = 0; i < studentPayments.size(); i++) {
            StudentPayment studentPayment = studentPayments.get(i);
            Label classLabel = new Label(studentPayment.getClassID());
            classLabel.setStyle("-fx-padding: 0 0 0 5;");

            Label subjectLabel = new Label(studentPayment.getSubjectName());
            subjectLabel.setStyle("-fx-padding: 0 0 0 5;");

            Label amountLabel = new Label(String.format("%.2f", Double.parseDouble(studentPayment.getAmount())));
            amountLabel.setStyle("-fx-padding: 0 0 0 5;");

            paymentGrid.add(classLabel, 0, i + 1);
            paymentGrid.add(subjectLabel, 1, i + 1);
            paymentGrid.add(amountLabel, 2, i + 1);
        }

        // Set total amount
        double total = getTotalPayment(studentPayments);
        totalLabel.setText(String.format("%.2f", total));
    }

    private double getTotalPayment(List<StudentPayment> studentPayments) {
        return studentPayments.stream()
                .mapToDouble(StudentPayment -> Double.parseDouble(StudentPayment.getAmount()))
                .sum();
    }

    private void clearGridRows(GridPane grid, int startRow) {
        grid.getChildren().removeIf(node -> GridPane.getRowIndex(node) != null && GridPane.getRowIndex(node) >= startRow);
    }
}
