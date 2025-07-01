package org.tcms.controller.AdminController;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import org.tcms.controller.BaseSideMenuController;
import org.tcms.navigation.View;

public class AdminSideMenuController extends BaseSideMenuController {
    @FXML private JFXButton monthlyReportBtn;
    @FXML private JFXButton staffManagementBtn;
    @FXML private VBox staffManagementSubMenu;
    @FXML private JFXButton registerBtn;
    @FXML private JFXButton updateProfileBtn;
    @FXML private JFXButton deleteProfileBtn;
    @FXML private JFXButton communicationBtn;
    @FXML private VBox sideMenu;

    @Override
    protected void onInit() {
        // toggle sub-menu
        setupToggle(staffManagementBtn, staffManagementSubMenu);

        // go-to page
        communicationBtn.setOnAction(e -> toolbarController.loadContent(View.COMMUNICATION, "Communication Hub"));
        registerBtn.setOnAction(e -> toolbarController.loadContent(View.REG_STAFF, "Register Staff & Assign Tutor"));
        monthlyReportBtn.setOnAction(e -> toolbarController.loadContent(View.MONTHLY_STATS, "Monthly Report"));
    }

    @Override
    protected void goHome(){
        toolbarController.loadContent(View.ADMIN_DASHBOARD, "Admin Dashboard");
    }
}
