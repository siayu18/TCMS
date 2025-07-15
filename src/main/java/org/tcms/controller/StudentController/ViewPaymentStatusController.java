package org.tcms.controller.StudentController;

import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import org.tcms.model.*;
import org.tcms.service.EnrollmentService;
import org.tcms.service.PaymentService;
import org.tcms.service.TuitionClassService;
import org.tcms.utils.AlertUtils;
import org.tcms.utils.Session;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ViewPaymentStatusController {
    @FXML private Label totalAmountLabel;
    @FXML private Label totalPaidLabel;
    @FXML private Label outstandingLabel;
    @FXML private GridPane paymentGrid;
    @FXML private PieChart paymentPie;


    private List<Payment> payments;
    private List<TuitionClass> classes;
    private List<Enrollment> enrollments;
    private double totalAmount;
    private double outstanding;
    private double totalPaid;

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

        // Get All Payments related to current student
        payments = paymentService.getAcceptedPayments().stream()
                .filter(row -> Session.getCurrentUserID().equalsIgnoreCase(row.getStudentID()))
                .collect(Collectors.toList());

        // get all enrollments related to current student
        enrollments = enrollmentService.getAllEnrollment().stream()
                .filter(e -> Session.getCurrentUserID().equalsIgnoreCase(e.getStudentID()))
                .collect(Collectors.toList());

        // get all classes that current student enrolled in
        classes = enrollments.stream()
                .map(row -> tuitionClassService.getClassByID(row.getClassID()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // Get The Payment's Info Needed
        totalAmount = classes.stream()
                .mapToDouble(row -> Double.parseDouble(row.getCharges()))
                .sum();
        totalPaid = payments.stream()
                .mapToDouble(row -> Double.parseDouble(row.getAmount()))
                .sum();
        outstanding = totalAmount - totalPaid;

        setLabel();
        setPaymentPie();
        setPaymentGrid();
    }

    private void setLabel() {
        totalAmountLabel.setText(String.format("Total Amount: RM %.2f", totalAmount));
        totalPaidLabel.setText(String.format("Total Paid (Accepted Payments): RM %.2f", totalPaid));
        outstandingLabel.setText(String.format("Outstanding: RM %.2f", outstanding));
    }

    private void setPaymentGrid() {
        for (int i = 0; i < classes.size(); i++) {
            TuitionClass tuitionClass = classes.get(i);

            // Get enrollmentID related to the class
            String enrollmentID = enrollments.stream()
                    .filter(e -> tuitionClass.getClassID().equalsIgnoreCase(e.getClassID()))
                    .map(Enrollment::getEnrollmentID)
                    .findFirst()
                    .orElse(null);

            // Skip if no enrollment found
            if (enrollmentID == null)
                continue;

            // Get the amount that is paid by student for the enrollment (class)
            double paidAmount = payments.stream()
                    .filter(row -> enrollmentID.equalsIgnoreCase(row.getEnrollmentID()))
                    .mapToDouble(row -> Double.parseDouble(row.getAmount()))
                    .sum();

            double classCharge = Double.parseDouble(tuitionClass.getCharges());

            // Set Label And Add to GridPane
            Label classIDLabel = new Label(tuitionClass.getClassID());
            classIDLabel.setStyle("-fx-padding: 0 0 0 5;");

            Label subjectLabel = new Label(tuitionClass.getSubjectName());
            subjectLabel.setStyle("-fx-padding: 0 0 0 5;");

            Label amountLabel = new Label(String.format("RM %.2f / RM %.2f", paidAmount, classCharge));
            amountLabel.setStyle("-fx-padding: 0 0 0 5;");

            paymentGrid.add(classIDLabel, 0, i + 1);
            paymentGrid.add(subjectLabel, 1, i + 1);
            paymentGrid.add(amountLabel, 2, i + 1);
        }
    }

    private void setPaymentPie() {
        PieChart.Data paidSlice = new PieChart.Data("Paid", totalPaid);
        PieChart.Data outstandingSlice = new PieChart.Data("Outstanding", outstanding);

        paymentPie.getData().addAll(paidSlice, outstandingSlice);

        paymentPie.setTitle("Payment Status");
        paymentPie.setLegendVisible(true);
        paymentPie.setLabelsVisible(true);
    }
}
