package org.tcms.controller.AdminController;

import javafx.fxml.FXML;
import org.tcms.controller.BaseDashboardController;

public class AdminDashController extends BaseDashboardController {

    @FXML
    public void initialize() {super.initialize();}

    @Override
    protected String formatGreeting(){return "Welcome Back Admin!";}
}

