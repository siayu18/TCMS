package org.tcms.controller.StudentController;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.tcms.exception.EmptyFieldException;
import org.tcms.exception.ValidationException;
import org.tcms.model.Enrollment;
import org.tcms.model.Payment;
import org.tcms.model.TuitionClass;
import org.tcms.service.EnrollmentService;
import org.tcms.service.PaymentService;
import org.tcms.service.TuitionClassService;
import org.tcms.utils.AlertUtils;
import org.tcms.utils.ComponentUtils;
import org.tcms.utils.Helper;
import org.tcms.utils.Session;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class PayController {
    public JFXComboBox<TuitionClass> chooseClassBox;
    public AnchorPane payPane;
    public Label payTitle;
    public TextField amountField;
    public Label previousAmountLabel;
    public Label amountLeftLabel;
    public JFXButton payBtn;
    public Label errorLabel;

    private List<Enrollment> enrollments;
    private List<TuitionClass> classes;
    private List<Payment> payments;
    private TuitionClass selectedClass;
    private String enrollmentID;
    private double amountPaid;
    private double remainingAmount;

    private EnrollmentService enrollmentService;
    private TuitionClassService tuitionClassService;
    private PaymentService paymentService;

    public void initialize() {
        try {
            enrollmentService = new EnrollmentService();
            tuitionClassService = new TuitionClassService();
            paymentService = new PaymentService();
        } catch (IOException e) {
            AlertUtils.showAlert("Data Loading Issue", "Failed to load data");
            return;
        }

        enrollments = enrollmentService.getAllEnrollment();
        payments = paymentService.getAllPayments();

        // Get only the classes that the students enrolled
        classes = enrollments.stream()
                .filter(row -> Session.getCurrentUserID().equalsIgnoreCase(row.getStudentID()))
                .map(row -> tuitionClassService.getClassByID(row.getClassID()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        ComponentUtils.configureClassBox(chooseClassBox, classes);
        configureActions();
    }

    private void configureActions() {
        payBtn.setOnAction(e -> {
            try {
                if (Helper.validateFieldNotEmpty(amountField)) {
                    throw new EmptyFieldException("Amount Cannot be Empty");
                }

                errorLabel.setVisible(false);
                double amount = Double.parseDouble(amountField.getText());

                if (amount > remainingAmount) {
                    throw new ValidationException("Invalid Amount, You cannot pay more than the remaining amount!");
                }

                paymentService.addPayment(new Payment(
                        UUID.randomUUID().toString(),
                        Session.getCurrentUserID(),
                        enrollmentID,
                        Double.toString(amount),
                        LocalDate.now().toString(),
                        LocalTime.now().withNano(0).toString(),
                        "unaccepted"
                ));

                payments = paymentService.getAllPayments(); // Reload the payment data

                // Recalculates the updated data and update the label
                amountPaid = getAmountPaid();
                remainingAmount = getRemainingAmount();
                setLabel(); // To Update the Label (To make it real-time)

                AlertUtils.showInformation("Payment Successful", "Successful: Please Kindly Wait for Receptionist to Approve Your Payment");

            } catch (EmptyFieldException | ValidationException ex) {
                errorLabel.setText(ex.getMessage());
                errorLabel.setVisible(true);
            }
        });

        chooseClassBox.setOnAction(e -> {
            selectedClass = chooseClassBox.getValue();
            if (selectedClass != null) {
                // get the enrollmentID for the selected class of the student
                enrollmentID = enrollments.stream()
                        .filter(row -> Session.getCurrentUserID().equalsIgnoreCase(row.getStudentID()))
                        .filter(row -> selectedClass.getClassID().equalsIgnoreCase(row.getClassID()))
                        .map(Enrollment::getEnrollmentID)
                        .findFirst()
                        .orElse(null);

                // get payment information and set payment label
                amountPaid = getAmountPaid();
                remainingAmount = getRemainingAmount();
                payTitle.setText("Pay Fees For "+ selectedClass.getSubjectName() + ":");
                setLabel();

                // set the pane and button as visible
                payPane.setVisible(true);
                payBtn.setVisible(true);
            }
        });
    }

    private double getAmountPaid() {
        if (selectedClass == null)
            return 0;

        // Get and sum all payments related to the enrollmentID of the user for the class
        return payments.stream()
                .filter(row -> enrollmentID.equalsIgnoreCase(row.getEnrollmentID()))
                .mapToDouble(row -> Double.parseDouble(row.getAmount()))
                .sum();
    }

    private double getRemainingAmount() {
        if (selectedClass == null)
            return 0;

        double selectedClassCharges = Double.parseDouble(selectedClass.getCharges());
        return selectedClassCharges - amountPaid;
    }

    private void setLabel() {
        previousAmountLabel.setText(String.format("You have paid RM %.2f for this class (Including Unaccepted Payments)", amountPaid));
        amountLeftLabel.setText(String.format("RM %.2f", remainingAmount));
    }
}
