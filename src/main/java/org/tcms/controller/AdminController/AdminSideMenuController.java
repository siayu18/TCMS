package org.tcms.controller.AdminController;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import org.tcms.controller.SystemController.BaseSideMenuController;
import org.tcms.navigation.View;

public class AdminSideMenuController extends BaseSideMenuController {
    @FXML private JFXButton registerProfileBtn;
    @FXML private JFXButton monthlyReportBtn;
    @FXML private JFXButton staffManagementBtn;
    @FXML private VBox staffManagementSubMenu;
    @FXML private JFXButton updateProfileBtn;

    @Override
    protected void onInit() {
        // toggle sub-menu
        setupToggle(staffManagementBtn, staffManagementSubMenu);

        // go-to page
        registerProfileBtn.setOnAction(e -> toolbarController.loadContent(View.REG_STAFF, "Register Staff & Assign Tutor"));
        monthlyReportBtn.setOnAction(e -> toolbarController.loadContent(View.MONTHLY_STATS, "Monthly Report"));
        updateProfileBtn.setOnAction(e -> toolbarController.loadContent(View.UPD_STAFF, "Update Staff Details"));

    }

    @Override
    protected void goHome(){
        toolbarController.loadContent(View.ADMIN_DASHBOARD, "Admin Dashboard");
    }
}
