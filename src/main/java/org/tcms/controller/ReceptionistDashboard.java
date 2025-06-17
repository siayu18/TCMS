package org.tcms.controller;

import org.tcms.component.ToggleButtonHelper;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;


public class ReceptionistDashboard {
    @FXML public Button testbtn3, testbtn4;

    @FXML
    public void initialize() {
        ToggleButtonHelper.setupToggle(List.of(testbtn3, testbtn4));
    }
}
