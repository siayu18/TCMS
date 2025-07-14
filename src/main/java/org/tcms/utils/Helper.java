package org.tcms.utils;

import javafx.scene.control.*;
import org.tcms.exception.EmptyFieldException;
import org.tcms.exception.ValidationException;
import org.tcms.model.TuitionClass;
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
        FileHandler accountFile = new FileHandler("account.csv", List.of("AccountID","Name","Password","Role"));
        List<Map<String, String>> rows = accountFile.readAll();

        int currentLastID = rows.stream()
                .map(row -> row.get("AccountID").replace("TP", ""))
                .mapToInt(Integer::parseInt)
                .max()
                .orElse(0);

        return String.format("TP%03d", currentLastID + 1);
    }

    public static boolean isTimeSlotOverlapping(String newDay, String newStartTime, String newEndTime, String currentClassID) throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mma");
        TuitionClassService tuitionClassService = new TuitionClassService();

        LocalTime newStart = LocalTime.parse(newStartTime.toUpperCase(), formatter);
        LocalTime newEnd = LocalTime.parse(newEndTime.toUpperCase(), formatter);

        for (TuitionClass cls : tuitionClassService.getClassesFromTutor()) {
            if (cls.getClassID().equals(currentClassID)) continue;
            if (!cls.getDay().equalsIgnoreCase(newDay)) continue;

            LocalTime existingStart = LocalTime.parse(cls.getStartTime().toUpperCase(), formatter);
            LocalTime existingEnd = LocalTime.parse(cls.getEndTime().toUpperCase(), formatter);

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
}
