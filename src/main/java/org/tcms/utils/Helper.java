package org.tcms.utils;

import javafx.scene.control.*;
import org.tcms.exception.EmptyFieldException;
import org.tcms.model.TuitionClass;

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

    public static boolean validatePassword(String password) {
        return password != null
                && password.length() >= 8
                && password.matches(".*[A-Z.*]")
                && password.matches(".*\\d.*")
                && password.matches(".*[!@#$%^&*].*")
                && password.matches(".*[a-z.*]");
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

    public static void isUsernamePasswordEmpty(TextField usernameField, TextField passwordField) throws EmptyFieldException {
        if (validateFieldNotEmpty(usernameField)) {
            throw new EmptyFieldException("Username Cannot be Empty!");
        }

        if (validateFieldNotEmpty(passwordField)) {
            throw new EmptyFieldException("Password Cannot be Empty!");
        }
    }

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
}
