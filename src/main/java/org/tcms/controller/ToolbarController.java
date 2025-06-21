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
                case "Admin" -> "/org/tcms/view/ReceptionistSideMenu.fxml";
                case "Student" -> "/org/tcms/view/ReceptionistSideMenu.fxml";
                case "Tutor" -> "/org/tcms/view/ReceptionistSideMenu.fxml";
                case "Receptionist" -> "/org/tcms/view/ReceptionistSideMenu.fxml";
                default -> "/org/tcms/view/LoginDashboard.fxml";
            };
            Parent sidePane = FXMLLoader.load(getClass().getResource(sideMenuPath));
            sidePane.setStyle("-fx-background-color: transparent;");
            drawer.setSidePane(sidePane);

            HamburgerBackArrowBasicTransition transition = new HamburgerBackArrowBasicTransition(hamburger);
            transition.setRate(-1);
            hamburger.addEventHandler(MouseEvent.MOUSE_PRESSED, (MouseEvent e) -> {
                transition.setRate(transition.getRate() * -1);
                transition.play();

                if (drawer.isOpened()) {
                    drawer.close();
                } else {
                    drawer.open();
                }

            });

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void loadRoleDashboard() {
        String dashboardPath = switch (userRole) {
            case "Admin" -> "/org/tcms/view/AdminDashboardView.fxml";
            case "Tutor" -> "/org/tcms/view/TutorDashboardView.fxml";
            case "Student" -> "/org/tcms/view/StudentDashboardView.fxml";
            case "Receptionist" -> "/org/tcms/view/ReceptionistDashboardView.fxml";
            default -> null;
        };

        if (dashboardPath != null) {
            SceneUtils.setContent(holderPane, dashboardPath);
        }
    }

    public void loadContent(String fileName) {
        SceneUtils.setContent(holderPane, "/org/tcms/view/" + fileName + ".fxml");
    }
}
