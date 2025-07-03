package org.tcms.controller.TutorController;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import org.tcms.controller.SystemController.BaseSideMenuController;
import org.tcms.navigation.View;

public class TutorSideMenuController extends BaseSideMenuController {
    @FXML private JFXButton updateClassDetails;
    @FXML private JFXButton viewStudentList;

    @Override
    protected void onInit() {
        // go-to page
        updateClassDetails.setOnAction(e -> toolbarController.loadContent(View.TUTOR_UPDATE_CLASS, "Update Class Details"));
        viewStudentList.setOnAction(e -> toolbarController.loadContent(View.TUTOR_VIEW_STD_LIST, "Student List"));

    }

    @Override
    protected void goHome(){
        toolbarController.loadContent(View.TUTOR_DASHBOARD, "Tutor Dashboard");
    }
}

