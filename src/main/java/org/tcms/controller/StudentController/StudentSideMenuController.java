package org.tcms.controller.StudentController;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import org.tcms.controller.SystemController.BaseSideMenuController;
import org.tcms.navigation.View;

public class StudentSideMenuController extends BaseSideMenuController {
    @FXML private JFXButton viewReceiptBtn;
    @FXML private JFXButton subjectManagementBtn;
    @FXML private VBox subManagementSubMenu;
    @FXML private JFXButton viewScheduleBtn;
    @FXML private JFXButton sendRequestBtn;
    @FXML private JFXButton delRequestBtn;
    @FXML private JFXButton paymentBtn;
    @FXML private VBox paymentSubMenu;
    @FXML private JFXButton payBtn;
    @FXML private JFXButton viewPaymentBtn;

    @Override
    protected void onInit() {
        // toggle sub-menu
        setupToggle(subjectManagementBtn, subManagementSubMenu);
        setupToggle(paymentBtn, paymentSubMenu);

        // go-to page
        viewScheduleBtn.setOnAction(e -> toolbarController.loadContent(View.VIEW_SCHEDULE, "View Schedule"));
        payBtn.setOnAction(e -> toolbarController.loadContent(View.PAY, "Pay Fees For Class"));
        viewPaymentBtn.setOnAction(e -> toolbarController.loadContent(View.VIEW_PAYMENT_STATUS, "View Payment Status"));
        viewReceiptBtn.setOnAction(e -> toolbarController.loadContent(View.VIEW_RECEIPT, "View Receipt"));
        sendRequestBtn.setOnAction(e -> toolbarController.loadContent(View.REQUEST_TRANSFER, "Request To Transfer Subjects"));
        delRequestBtn.setOnAction(e -> toolbarController.loadContent(View.DELETE_REQUEST, "Delete Subject Transfer Request"));
    }

    @Override
    protected void goHome() {
        toolbarController.loadContent(View.STUDENT_DASHBOARD, "Student Dashboard");
    }
}
