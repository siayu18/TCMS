package org.tcms.component;

import javafx.scene.control.Button;
import java.util.List;

public class ToggleButtonHelper {

    private static Button currentActiveButton = null;

    public static void setupToggle(List<Button> buttons) {
        for (Button button : buttons) {
            button.setOnAction(e -> {
                if (currentActiveButton != null) {
                    currentActiveButton.setStyle("-fx-font-weight: normal;");
                }
                currentActiveButton = button;
                currentActiveButton.setStyle("-fx-font-weight: bold;");
            });
        }
    }
}