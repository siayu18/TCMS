package org.tcms.controller;

import javafx.fxml.FXML;


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