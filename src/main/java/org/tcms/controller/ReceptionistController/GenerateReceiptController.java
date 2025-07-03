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
import org.tcms.utils.ComponentUtils;
import org.tcms.utils.MappingUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GenerateReceiptController {

    public Button generateBtn;
    public Label errorLabel;
    public AnchorPane receiptPane;
    public Label title;
    public ScrollPane scrollBar;
    public VBox receiptBox;
    public JFXComboBox chooseStudentBox;

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
            errorLabel.setText("Failed to load data.");
            errorLabel.setVisible(true);
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
        // Clear old receipt and set padding and insects
        receiptBox.getChildren().clear();
        receiptBox.setPadding(new Insets(20));
        receiptBox.setSpacing(15);

        // Header
        VBox header = new VBox(5);
        header.setAlignment(Pos.CENTER);
        Label centerName = new Label("Tuition Centre");
        centerName.getStyleClass().add("label-heading-large");
        Label address = new Label("123 Bukit Jalil, APU, 53000 Kuala Lumpur");
        address.getStyleClass().add("label-text-regular");
        Label dateLabel = new Label("Date: " +
                java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy"))
        );
        Label studentLabel = new Label("Student: " + selectedStudent.getUsername());
        header.getChildren().addAll(centerName, address, dateLabel, studentLabel);

        // Print each payment
        GridPane paymentGrid = new GridPane();
        paymentGrid.setHgap(20);
        paymentGrid.setVgap(8);
        paymentGrid.setPadding(new Insets(10, 0, 10, 0));

        // Header row
        Label c1 = new Label("Class ID"), c2 = new Label("Subject"), c3 = new Label("Amount");
        c1.getStyleClass().add("label-heading-small");
        c2.getStyleClass().add("label-heading-small");
        c3.getStyleClass().add("label-heading-small");
        paymentGrid.add(c1, 0, 0);
        paymentGrid.add(c2, 1, 0);
        paymentGrid.add(c3, 2, 0);

        // Data rows
        List<StudentPayment> studentPayments = MappingUtils.mapPaymentsForStudent(selectedStudent, payments, enrollmentMap, classMap);
        for (int i = 0; i < studentPayments.size(); i++) {
            StudentPayment studentPayment = studentPayments.get(i);
            paymentGrid.add(new Label(studentPayment.getClassID()), 0, i+1);
            paymentGrid.add(new Label(studentPayment.getSubjectName()), 1, i+1);
            paymentGrid.add(new Label(String.format("%.2f", Double.parseDouble(studentPayment.getAmount()))), 2, i+1);
        }

        // Print total
        HBox totalRow = new HBox(10);
        double total = getTotalPayment(studentPayments);
        Label totalLabel = new Label("Total Paid: " + String.format("%.2f", total));
        totalLabel.getStyleClass().add("label-heading-small");
        totalRow.getChildren().add(totalLabel);

        receiptBox.getChildren().addAll(
                header,
                new Separator(),
                paymentGrid,
                new Separator(),
                totalRow
        );
    }

    private double getTotalPayment(List<StudentPayment> studentPayments) {
        return studentPayments.stream()
                .mapToDouble(StudentPayment -> Double.parseDouble(StudentPayment.getAmount()))
                .sum();
    }
}
