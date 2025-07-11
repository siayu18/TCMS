package org.tcms.controller.SystemController;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import lombok.Setter;
import org.tcms.navigation.View;

public abstract class BaseSideMenuController {
    @FXML
    protected JFXButton homeBtn, logoutBtn, exitBtn, communicationBtn;

    @Setter
    protected ToolbarController toolbarController;

    @FXML
    private void initialize() {
        homeBtn.setOnAction(e -> goHome());
        logoutBtn.setOnAction(e -> logout());
        exitBtn.setOnAction(e -> exitApp());
        communicationBtn.setOnAction(e -> communicationHub());
        onInit();
    }

    protected abstract void goHome();

    protected void logout() {
        toolbarController.loadWholeScene(View.LOGIN);
    }

    protected void exitApp() {
        System.exit(0);
    }

    protected void communicationHub() { toolbarController.loadContent(View.COMMUNICATION, "Communication Hub");}

    protected void setupToggle(JFXButton triggerButton, VBox submenu) {
        triggerButton.setOnAction(e -> {
            boolean isOpen = submenu.isVisible();
            submenu.setVisible(!isOpen);
            submenu.setManaged(!isOpen);
        });
    }

    protected void onInit() {}
}
