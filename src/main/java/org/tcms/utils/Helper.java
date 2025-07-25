package org.tcms.utils;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import org.tcms.exception.EmptyFieldException;
import org.tcms.exception.ValidationException;
import org.tcms.model.*;
import org.tcms.service.TuitionClassService;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Helper {
    // Make string capitalize
    public static String capitalize(String input) {
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }

    // validate textfield empty or not
    public static boolean validateFieldNotEmpty(TextInputControl input) {
        return input.getText().trim().isEmpty();
    }

    // validate combobox empty or not
    public static boolean validateComboBoxNotEmpty(ComboBox<?> comboBox) {
        return comboBox.getValue() == null || comboBox.getValue().toString().trim().isEmpty();
    }

    // validate datepicker empty or not
    public static boolean validateDatePickerNotEmpty(DatePicker datePicker) {
        if (datePicker.getValue() == null)
            return true;
        else
            return false;
    }

    // validate password format
    public static boolean validatePassword(String password) {
        if (password.length() < 8) {
            return false;
        }

        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUpper = true;
            } else if (Character.isLowerCase(c)) {
                hasLower = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            } else if (!Character.isLetterOrDigit(c)) {
                hasSpecial = true;
            }
        }
        return hasUpper && hasLower && hasDigit && hasSpecial;
    }

    public static boolean validateIC(String ic) {
        return ic.matches("\\d{6}-\\d{2}-\\d{4}");
    }

    public static boolean validateEmail(String email) {
        return email.contains("@");
    }

    public static boolean validateContact(String contact) {
        if (!contact.matches("\\d+")) {
            return false;
        }
        return true;
    }

    public static void isUsernamePasswordEmpty(TextField usernameField, TextField passwordField) throws EmptyFieldException {
        if (validateFieldNotEmpty(usernameField)) {
            throw new EmptyFieldException("Username Cannot be Empty!");
        }

        if (validateFieldNotEmpty(passwordField)) {
            throw new EmptyFieldException("Password Cannot be Empty!");
        }
    }

    // Check whether there is duplication on selection
    public static boolean hasDuplicateClassSelections(TuitionClass... classes) {
        List<String> selectedIDs = new ArrayList<>();

        for (TuitionClass tuitionClass : classes) {
            if (tuitionClass != null) {
                selectedIDs.add(tuitionClass.getClassID());
            }
        }

        Set<String> uniqueIDs = new HashSet<>(selectedIDs); // set auto remove duplication
        return uniqueIDs.size() < selectedIDs.size();
    }

    // get a new accountID
    public static String generateAccountID() {
        FileHandler accountFile = new FileHandler("account.csv", List.of("AccountID", "Name", "Password", "Role"));
        List<Map<String, String>> rows = accountFile.readAll();

        int currentLastID = rows.stream()
                .map(row -> row.get("AccountID").replace("TP", ""))
                .mapToInt(Integer::parseInt)
                .max()
                .orElse(0);

        return String.format("TP%03d", currentLastID + 1);
    }

    public static boolean isTimeSlotOverlapping(String newDay, String newStartTime, String newEndTime, String currentClassID) throws IOException {
        newStartTime = newStartTime.trim().toLowerCase();
        newEndTime = newEndTime.trim().toLowerCase();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mma");
        TuitionClassService tuitionClassService = new TuitionClassService();

        LocalTime newStart = LocalTime.parse(newStartTime, formatter);
        LocalTime newEnd = LocalTime.parse(newEndTime, formatter);

        for (TuitionClass cls : tuitionClassService.getClassesFromTutor()) {
            if (cls.getClassID().equals(currentClassID)) continue;
            if (!cls.getDay().equalsIgnoreCase(newDay)) continue;

            String clsStartTime = cls.getStartTime().trim().toLowerCase();
            String clsEndTime = cls.getEndTime().trim().toLowerCase();

            LocalTime existingStart = LocalTime.parse(clsStartTime, formatter);
            LocalTime existingEnd = LocalTime.parse(clsEndTime, formatter);

            boolean isOverlapping = newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart);

            if (isOverlapping) {
                return true;
            }
        }
        return false;
    }

    public static void isClassInfoValid(String classID, String charges, String startTime, String endTime, String day) throws ValidationException, IOException {
        if (!charges.matches("\\d+")) {
            throw new ValidationException("Charges must be an Integer Value!");
        }

        String timeRegex = "^(0?[1-9]|1[0-2]):[0-5][0-9](am|pm|AM|PM)$";

        if (!startTime.matches(timeRegex)) {
            throw new ValidationException("Start Time must be in format hh:mmam/pm (e.g., 9:00am)");
        }

        if (!endTime.matches(timeRegex)) {
            throw new ValidationException("End Time must be in format hh:mmam/pm (e.g., 10:00am)");
        }

        if (Helper.isTimeSlotOverlapping(day, startTime, endTime, classID)) {
            throw new ValidationException("This time slot on " + day + " is already taken.");
        }
    }

    public static String generateClassID() {
        FileHandler classFile = new FileHandler("tuitionclass.csv", List.of("ClassID", "TutorID", "SubjectName", "Information", "Charges", "Day", "StartTime", "EndTime", "Level"));
        List<Map<String, String>> rows = classFile.readAll();

        int currentLastID = rows.stream()
                .map(row -> row.get("ClassID").replace("CL", ""))
                .mapToInt(Integer::parseInt)
                .max()
                .orElse(0);

        return String.format("CL%03d", currentLastID + 1);
    }

    public static void renderReceipt(VBox receiptBox, Label dateLabel, Label studentLabel, Label totalLabel, GridPane paymentGrid,
                                     Student selectedStudent, List<Payment> payments, Map<String, Enrollment> enrollmentMap, Map<String, TuitionClass> classMap) {
        receiptBox.setPadding(new Insets(20));
        receiptBox.setSpacing(15);

        // Set Header
        String currentDate = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy"));
        dateLabel.setText("Date: " + currentDate);
        studentLabel.setText("Student: " + selectedStudent.getUsername());

        // Add Payment Data Rows
        clearGridRows(paymentGrid, 1); // Clear rows every render to avoid old data leaving there when a new student is selected
        List<StudentPaymentEntry> studentPayments = MappingUtils.mapPaymentsForStudent(selectedStudent, payments, enrollmentMap, classMap);

        for (int i = 0; i < studentPayments.size(); i++) {
            StudentPaymentEntry studentPayment = studentPayments.get(i);
            Label classLabel = new Label(studentPayment.getClassID());
            classLabel.setStyle("-fx-padding: 0 0 0 5;");

            Label subjectLabel = new Label(studentPayment.getSubjectName());
            subjectLabel.setStyle("-fx-padding: 0 0 0 5;");

            Label amountLabel = new Label(String.format("RM %.2f", Double.parseDouble(studentPayment.getAmount())));
            amountLabel.setStyle("-fx-padding: 0 0 0 5;");

            paymentGrid.add(classLabel, 0, i + 1);
            paymentGrid.add(subjectLabel, 1, i + 1);
            paymentGrid.add(amountLabel, 2, i + 1);

            // For fixed heights for each row
            RowConstraints rc = new RowConstraints();
            rc.setPrefHeight(30);
            paymentGrid.getRowConstraints().add(rc);
        }

        // Set total amount
        double total = getTotalPayment(studentPayments);
        totalLabel.setText(String.format("RM %.2f", total));
    }

    private static double getTotalPayment(List<StudentPaymentEntry> studentPayments) {
        return studentPayments.stream()
                .mapToDouble(StudentPayment -> Double.parseDouble(StudentPayment.getAmount()))
                .sum();
    }

    private static void clearGridRows(GridPane grid, int startRow) {
        grid.getChildren().removeIf(node -> GridPane.getRowIndex(node) != null && GridPane.getRowIndex(node) >= startRow);
    }

}
