package org.tcms.controller.StudentController;

import org.tcms.component.ToggleButtonHelper;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;


public class StudentDashboard {
    @FXML public Button testbtn5, testbtn6;

    @FXML
    public void initialize() {
        ToggleButtonHelper.setupToggle(List.of(testbtn5, testbtn6));
    }
}
