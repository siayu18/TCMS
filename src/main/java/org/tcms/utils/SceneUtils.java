package org.tcms.utils;

import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import org.tcms.controller.ToolbarController;
import javafx.fxml.FXMLLoader;

import java.io.IOException;

public class SceneUtils {

    // load the fxml file
    public static Node loadFxml(String fxmlPath, Object controller) throws IOException {
        FXMLLoader loader = new FXMLLoader(SceneUtils.class.getResource(fxmlPath));
        if (controller != null) {
            loader.setController(controller);
        }
        return loader.load();
    }

    // clear the AnchorPane and add new screen (content)
    private static void applyAnchor(AnchorPane holder, Node node) {
        holder.getChildren().setAll(node);
        AnchorPane.setTopAnchor(node, 0.0);
        AnchorPane.setRightAnchor(node, 0.0);
        AnchorPane.setBottomAnchor(node, 0.0);
        AnchorPane.setLeftAnchor(node, 0.0);
    }

    // set the content of the sidebar and dashboard based on role
    public static void setSideBarAndDashboard(AnchorPane holderPane, String fxmlPath, String role) {
        try {
            ToolbarController controller = new ToolbarController(role);
            Node sideBar = loadFxml(fxmlPath, controller);
            applyAnchor(holderPane, sideBar);
            controller.loadRoleDashboard();

        } catch (IOException e) {
            e.printStackTrace();
            AlertUtils.showAlert("Load Error", "Could not load: " + fxmlPath);
        }
    }

    // set the content of the AnchorPane (holderPane)
    public static void setContent(AnchorPane holderPane, String fxmlPath) {
        try {
            Node node = loadFxml(fxmlPath, null);
            applyAnchor(holderPane, node);
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtils.showAlert("Load Error", "Could not load: " + fxmlPath);
        }
    }

    // remove screen background colour
    public static void clearScreenColor(AnchorPane pane) {
        pane.setBackground(null);
    }
}
