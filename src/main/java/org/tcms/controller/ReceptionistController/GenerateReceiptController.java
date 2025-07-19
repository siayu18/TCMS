package org.tcms.controller.ReceptionistController;

import com.jfoenix.controls.JFXComboBox;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.tcms.model.*;
import org.tcms.service.*;
import org.tcms.utils.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GenerateReceiptController {

    @FXML private Button generateBtn;
    @FXML private Label errorLabel;
    @FXML private AnchorPane receiptPane;
    @FXML private VBox receiptBox;
    @FXML private JFXComboBox<Student> chooseStudentBox;
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

        // Make it into {id:object of id} to make it easier for mapping
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
            selectedStudent = chooseStudentBox.getValue();
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

            Helper.renderReceipt(
                    receiptBox, dateLabel, studentLabel, totalLabel, paymentGrid,
                    selectedStudent, payments, enrollmentMap, classMap
            );
        });
    }
}
