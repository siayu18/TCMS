package org.tcms.controller.AdminController;

import com.jfoenix.controls.JFXButton;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.tcms.model.MonthlyReportEntry;
import org.tcms.model.Payment;
import org.tcms.model.TuitionClass;
import org.tcms.service.EnrollmentService;
import org.tcms.service.MonthlyReportService;
import org.tcms.service.PaymentService;
import org.tcms.service.TuitionClassService;
import org.tcms.utils.AlertUtils;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;


public class MonthlyReportController {
    // FXML components
    @FXML private Label incomeTotal;
    @FXML private ComboBox<String> selectMonth, selectLevel, selectSubject;
    @FXML private TableView<MonthlyReportEntry> reportTable;
    @FXML private TableColumn<MonthlyReportEntry, String> subjectColumn;
    @FXML private TableColumn<MonthlyReportEntry, String> levelColumn;
    @FXML private TableColumn<MonthlyReportEntry, Double> incomeColumn;
    @FXML private JFXButton viewReportBtn;

    // Services & Variables
    private PaymentService paymentService;
    private EnrollmentService enrollmentService;
    private TuitionClassService tuitionClassService;
    private MonthlyReportService monthlyReportService;


    @FXML
    public void initialize() {
        try {
            paymentService = new PaymentService();
            enrollmentService = new EnrollmentService();
            tuitionClassService = new TuitionClassService();
            monthlyReportService = new MonthlyReportService(paymentService, enrollmentService, tuitionClassService);

            loadMonthComboBox();
            loadSubjectComboBox();
            loadLevelComboBox();
            configureTable();

            viewReportBtn.setOnAction(e -> generateAndDisplayReport());


        } catch (IOException e) {
            AlertUtils.showAlert("Error","Failed to load payment data: ");
        }
    }


    private void loadMonthComboBox() {
        List<Payment> payments = paymentService.getAllPayments();

        List<String> uniqueMonths = payments.stream()
                .map(Payment::getDate)
                .filter(dateStr -> dateStr != null && !dateStr.isEmpty())
                .map(dateStr -> {
                    // Parse month from "dd-MM-yyyy" (e.g., "06" → "June")
                    String[] parts = dateStr.split("-");
                    if (parts.length >= 2) {
                        int monthNum = Integer.parseInt(parts[1]);
                        return java.time.Month.of(monthNum).getDisplayName(
                                java.time.format.TextStyle.FULL,
                                java.util.Locale.ENGLISH
                        );
                    }
                    return "Invalid Date";
                })
                .distinct()
                .sorted()
                .toList();

        selectMonth.getItems().addAll(uniqueMonths);
        selectMonth.getItems().addFirst("All"); // Add "All" option
    }

    private void loadSubjectComboBox() {
        List<Payment> payments = paymentService.getAllPayments();

        List<String> uniqueSubjects = payments.stream()
                // Get EnrollmentID from payment
                .map(Payment::getEnrollmentID)
                .map(enrollmentID -> enrollmentService.getEnrollmentByID(enrollmentID))
                .filter(Objects::nonNull)
                // Get the TuitionClass using the enrollment's classID
                .map(enrollment -> tuitionClassService.getClassByID(enrollment.getClassID())) // Now getClassID() works
                .filter(Objects::nonNull)
                // Extract subject name
                .map(TuitionClass::getSubjectName)
                .distinct()
                .sorted()
                .toList();

        selectSubject.getItems().addAll(uniqueSubjects);
        selectSubject.getItems().addFirst("All");
    }

    private void loadLevelComboBox() {
        List<Payment> payments = paymentService.getAllPayments();

        List<String> uniqueLevels = payments.stream()
                .map(Payment::getEnrollmentID)
                .map(enrollmentID -> enrollmentService.getEnrollmentByID(enrollmentID))
                .filter(Objects::nonNull)

                .map(enrollment -> tuitionClassService.getClassByID(enrollment.getClassID()))
                .filter(Objects::nonNull)

                .map(TuitionClass::getLevel) // Extract level
                .distinct()
                .sorted()
                .toList();

        selectLevel.getItems().addAll(uniqueLevels);
        selectLevel.getItems().addFirst("All");
    }

    private void configureTable() {
        // Subject column
        subjectColumn.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getSubject())
        );

        // Level column
        levelColumn.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getLevel())
        );

        // Income column (with currency formatting)
        incomeColumn.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getTotalIncome()).asObject()
        );
        incomeColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double income, boolean empty) {
                super.updateItem(income, empty);
                if (empty || income == null) {
                    setText(null);
                } else {
                    setText(String.format("RM%.2f", income));
                }
            }
        });
    }

    private void generateAndDisplayReport() {
        String selectedMonth = selectMonth.getValue();
        String selectedSubject = selectSubject.getValue();
        String selectedLevel = selectLevel.getValue();

        // Generate report data
        Collection<? extends MonthlyReportEntry> reportData = monthlyReportService.generateReport(
                selectedMonth,
                selectedSubject,
                selectedLevel
        );

        // Update table
        reportTable.setItems(javafx.collections.FXCollections.observableArrayList(reportData));

        // Update total income
        double total = reportData.stream()
                .mapToDouble(MonthlyReportEntry::getTotalIncome)
                .sum();
        incomeTotal.setText("RM " + total);

        if (reportData.isEmpty()) {
            AlertUtils.showInformation("No Data", "No payments match the selected filters.");
        }
    }


}


