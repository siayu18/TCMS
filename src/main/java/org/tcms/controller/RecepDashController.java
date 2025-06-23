package org.tcms.controller;

import org.tcms.component.ToggleButtonHelper;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;


public class RecepDashController {
    @FXML public Button updateStuBtn, acceptPayBtn, genRecBtn, delStuBtn, comHubBtn, profileBtn, addStuBtn;

    @FXML
    public void initialize() {
        ToggleButtonHelper.setupToggle(List.of(updateStuBtn, acceptPayBtn, genRecBtn, delStuBtn, comHubBtn, profileBtn));
    }
}