package org.tcms.controller.ReceptionistController;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import org.tcms.controller.SystemController.BaseSideMenuController;
import org.tcms.navigation.View;

public class RecepSideMenuController extends BaseSideMenuController {
    @FXML private JFXButton viewStudentRequestBtn;
    @FXML private JFXButton viewHistoryBtn;
    @FXML private JFXButton stuMangementBtn;
    @FXML private VBox stuManagementSubMenu;
    @FXML private JFXButton registerBtn;
    @FXML private JFXButton updDelStudentBtn;
    @FXML private JFXButton updateEnrolmentBtn;
    @FXML private JFXButton paymentBtn;
    @FXML private VBox paymentSubMenu;
    @FXML private JFXButton acceptPaymentBtn;
    @FXML private JFXButton generateRcptBtn;

    @Override
    protected void onInit() {
        // toggle sub-menu
        setupToggle(stuMangementBtn, stuManagementSubMenu);
        setupToggle(paymentBtn, paymentSubMenu);

        // go-to page
        updDelStudentBtn.setOnAction(e -> toolbarController.loadContent(View.UPD_DEL_STUDENT, "Update & Delete Student"));
        registerBtn.setOnAction(e -> toolbarController.loadContent(View.REGISTER_ENROLL_STUDENT, "Register & Enroll Student"));
        updateEnrolmentBtn.setOnAction(e -> toolbarController.loadContent(View.UPD_ENROLMENT, "Update Student Enrollment"));
        acceptPaymentBtn.setOnAction(e -> toolbarController.loadContent(View.ACCEPT_PAYMENT, "Accept Student's Payment"));
        generateRcptBtn.setOnAction(e -> toolbarController.loadContent(View.GENERATE_RECEIPT, "Generate Receipt"));
        viewHistoryBtn.setOnAction(e -> toolbarController.loadContent(View.VIEW_PAYMENT_HISTORY, "View Payment History"));
        viewStudentRequestBtn.setOnAction(e -> toolbarController.loadContent(View.VIEW_TRANSFER_REQUEST, "View Student Transfer Request"));
    }

    @Override
    protected void goHome() {
        toolbarController.loadContent(View.RECEP_DASHBOARD, "Receptionist Dashboard");
    }
}
