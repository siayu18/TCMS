package org.tcms.controller;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

public class RecepSideBarController {
    @FXML private VBox paymentSubMenu;

    @FXML
    private void togglePaymentMenu() {
        boolean showing = paymentSubMenu.isVisible();
        paymentSubMenu.setVisible(!showing);
        paymentSubMenu.setManaged(!showing);
    }
}
