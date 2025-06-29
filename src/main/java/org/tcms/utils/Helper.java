package org.tcms.utils;

import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputControl;

import java.util.List;
import java.util.Map;

public class Helper {
    // Make string capitalize
    public static String capitalize(String input) {
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }

    // validate textfield empty or not
    public static boolean validateFieldNotEmpty(TextInputControl input, Label label, String message) {
        if (input.getText().trim().isEmpty()) {
            label.setText(message);
            return true;
        } else {
            return false;
        }
    }

    // validate combobox empty or not
    public static boolean validateComboBoxNotEmpty(ComboBox<?> comboBox, Label label, String message) {
        if (comboBox.getValue() == null || comboBox.getValue().toString().trim().isEmpty()) {
            label.setText(message);
            return true;
        } else {
            return false;
        }
    }

    // validate datepicker empty or not
    public static boolean validateDatePickerNotEmpty(DatePicker datePicker, Label label, String message) {
        if (datePicker.getValue() == null) {
            label.setText(message);
            return true;
        } else {
            return false;
        }
    }

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
        for (char c: contact.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    // get the accountID
    public static String generateAccountID() {
        FileHandler accountFile = new FileHandler("account.csv", List.of("AccountID","Name","Password","Role"));
        List<Map<String, String>> rows = accountFile.readAll();

        int currentLastID = rows.stream()
                .map(row -> row.get("AccountID").replace("TP", ""))
                .mapToInt(Integer::parseInt)
                .max()
                .orElse(0);
        int newID =  currentLastID + 1;

        return String.format("TP%03d", currentLastID + 1);
    }



}
