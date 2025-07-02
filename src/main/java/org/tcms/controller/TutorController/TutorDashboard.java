package org.tcms.controller.TutorController;

import javafx.fxml.FXML;
import org.tcms.controller.SystemController.BaseDashboardController;


public class TutorDashboard extends BaseDashboardController {

    @FXML
    public void initialize() {super.initialize();}

    @Override
    protected String formatGreeting(){return "Welcome Back Tutor!";}
}


