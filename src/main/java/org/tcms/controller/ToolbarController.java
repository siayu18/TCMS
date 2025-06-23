package org.tcms.controller;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import org.tcms.utils.SceneUtils;
import org.tcms.navigation.Role;
import org.tcms.navigation.View;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ToolbarController implements Initializable {
    @FXML private JFXHamburger hamburger;
    @FXML private JFXDrawer drawer;
    @FXML public AnchorPane mainPane;
    @FXML private Label txtCurrentWindow;
    @FXML private AnchorPane holderPane;

    private Role role;

    // invoke this manually after login to get the role of user and initialize the dashboard and side menu
    public void initializeWith(Role role) {
        this.role = role;

        setupDrawer();
        SceneUtils.setContent(holderPane, role.getDashboard());
        txtCurrentWindow.setText(role.name() + " Dashboard");
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    // setup side menu and animations etc.
    private void setupDrawer() {
        // load the side-menu FXML into a Node
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(role.getSideMenu().getPath()));
            Node sideMenuNode = loader.load();
            BaseSideMenuController sideController = loader.getController();
            sideController.setToolbarController(this);
            drawer.setSidePane(sideMenuNode);
        } catch (IOException e) {
            throw new RuntimeException("Could not load side menu: " + role.getSideMenu(), e);
        }

        drawer.setMouseTransparent(true);

        HamburgerBackArrowBasicTransition t = new HamburgerBackArrowBasicTransition(hamburger);
        t.setRate(-1);
        hamburger.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            t.setRate(t.getRate() * -1);
            t.play();
            if (drawer.isOpened()) {
                drawer.close();
                drawer.setMouseTransparent(true);
            } else {
                drawer.open();
                drawer.setMouseTransparent(false);
            }
        });
    }

    // to swap content for the holderPane
    public void loadContent(View view, String title) {
        SceneUtils.setContent(holderPane, view);
        txtCurrentWindow.setText(title);
    }

    // to swap content for the mainPane
    public void loadWholeScene(View view) {
        SceneUtils.setContent(mainPane, view);
    }
}
