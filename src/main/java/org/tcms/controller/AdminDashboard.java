package org.tcms.controller;

import org.tcms.component.ToggleButtonHelper;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.List;

public class AdminDashboard {
    @FXML public Button testbtn1, testbtn2;

    @FXML
    public void initialize() {
        ToggleButtonHelper.setupToggle(List.of(testbtn1, testbtn2));
    }
}

