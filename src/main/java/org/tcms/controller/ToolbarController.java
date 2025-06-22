package org.tcms.controller;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import org.tcms.utils.SceneUtils;

public class ToolbarController implements Initializable {
    @FXML private JFXHamburger hamburger;
    @FXML private AnchorPane holderPane;
    @FXML private JFXDrawer drawer;
    @FXML private Label txtCurrentWindow;
    private final String userRole;

    public ToolbarController(String userRole) {
        this.userRole = userRole;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            String sideMenuPath = switch (userRole) {
                case "Admin" -> "ReceptionistSideMenu";
                case "Student" -> "ReceptionistSideMenu";
                case "Tutor" -> "ReceptionistSideMenu";
                case "Receptionist" -> "ReceptionistSideMenu";
                default -> "LoginView";
            };
            Parent sidePane = FXMLLoader.load(getClass().getResource("/org/tcms/view/" + sideMenuPath + ".fxml"));
            drawer.setSidePane(sidePane);
            drawer.setMouseTransparent(true); // to fix unable to tap button on the drawer zone though closed

            HamburgerBackArrowBasicTransition transition = new HamburgerBackArrowBasicTransition(hamburger);
            transition.setRate(-1);
            hamburger.addEventHandler(MouseEvent.MOUSE_PRESSED, (MouseEvent e) -> {
                transition.setRate(transition.getRate() * -1);
                transition.play();

                if (drawer.isOpened()) {
                    drawer.close();
                    drawer.setMouseTransparent(true); // to fix unable to tap button on the drawer zone though closed
                } else {
                    drawer.open();
                    drawer.setMouseTransparent(false); // to fix unable to tap button on the drawer zone though closed
                }

            });

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void loadRoleDashboard() {
        String dashboardFileName = switch (userRole) {
            case "Admin" -> "AdminDashboardView";
            case "Tutor" -> "TutorDashboardView";
            case "Student" -> "StudentDashboardView";
            case "Receptionist" -> "ReceptionistDashboardView";
            default -> null;
        };

        if (dashboardFileName != null) {
            SceneUtils.setContent(holderPane, dashboardFileName);
        }
    }

    public void loadContent(String fileName) {
        SceneUtils.setContent(holderPane, "/org/tcms/view/" + fileName + ".fxml");
    }
}
