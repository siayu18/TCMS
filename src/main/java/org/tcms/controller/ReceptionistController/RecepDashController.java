package org.tcms.controller.ReceptionistController;

import javafx.fxml.FXML;
import org.tcms.controller.BaseDashboardController;


public class RecepDashController extends BaseDashboardController {

    @FXML
    public void initialize() {
        super.initialize();
    }

    @Override
    protected String formatGreeting() {
        return "Welcome Back Receptionist!";
    }
}