package org.tcms.controller;

import org.tcms.component.ToggleButtonHelper;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;


public class TutorDashboard {
    @FXML public Button testbtn7, testbtn8;

    @FXML
    public void initialize() {
        ToggleButtonHelper.setupToggle(List.of(testbtn7, testbtn8));
    }
}
