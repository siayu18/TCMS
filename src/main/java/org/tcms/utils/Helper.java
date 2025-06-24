package org.tcms.utils;

import javafx.scene.control.Label;
import javafx.scene.control.TextInputControl;

public class Helper {
    // Make string capitalize
    public static String capitalize(String input) {
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }

    // validate empty or not
    public static boolean validateNotEmpty(TextInputControl input, Label label, String message) {
        if (input.getText().trim().isEmpty()) {
            label.setText(message);
            return true;
        } else {
            return false;
        }
    }

}
