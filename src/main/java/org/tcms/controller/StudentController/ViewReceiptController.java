package org.tcms.controller.StudentController;

import com.jfoenix.controls.JFXComboBox;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.tcms.model.Enrollment;
import org.tcms.model.Payment;
import org.tcms.model.Student;
import org.tcms.model.TuitionClass;
import org.tcms.service.*;
import org.tcms.utils.AlertUtils;
import org.tcms.utils.Helper;
import org.tcms.utils.Session;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ViewReceiptController {
    public AnchorPane receiptPane;
    public VBox receiptBox;
    public Label dateLabel;
    public Label studentLabel;
    public GridPane paymentGrid;
    public Label totalLabel;
    public Label errorLabel;

    private List<Payment> payments;
    private Map<String, TuitionClass> classMap;
    private Map<String, Enrollment> enrollmentMap;

    private TuitionClassService tuitionClassService;
    private EnrollmentService enrollmentService;
    private PaymentService paymentService;
    private ReceiptService receiptService;

    @FXML
    public void initialize() {
        try {
            tuitionClassService = new TuitionClassService();
            enrollmentService = new EnrollmentService();
            paymentService = new PaymentService();
            receiptService = new ReceiptService();
        } catch (IOException e) {
            AlertUtils.showAlert("Data Loading Issue", "Failed to load data");
            return;
        }

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

        displayReceiptIfExists();
    }

    private void displayReceiptIfExists() {
        if (receiptService.isReceiptGenerated((Student) Session.getCurrentUser())) {
            receiptBox.setVisible(true);
            errorLabel.setVisible(false);
            Helper.renderReceipt(
                    receiptBox, dateLabel, studentLabel, totalLabel, paymentGrid,
                    (Student) Session.getCurrentUser(), payments, enrollmentMap, classMap
            );
        } else {
            errorLabel.setText("Receipt has not been generated before!");
            receiptBox.setVisible(false);
            errorLabel.setVisible(true);
        }
    }
}
