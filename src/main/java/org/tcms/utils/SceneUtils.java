package org.tcms.utils;

import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import org.tcms.controller.ToolbarController;
import javafx.fxml.FXMLLoader;

import java.io.IOException;

public class SceneUtils {
    public static void setSideBarAndDashboard(AnchorPane holderPane, String fxmlPath, String role) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneUtils.class.getResource(fxmlPath));
            // Inject controller with role
            ToolbarController controller = new ToolbarController(role);
            loader.setController(controller);

            Node node = loader.load();

            // Clear old content
            holderPane.getChildren().clear();

            // Add new content
            holderPane.getChildren().add(node);

            // Anchor new content to all edges
            AnchorPane.setTopAnchor(node, 0.0);
            AnchorPane.setRightAnchor(node, 0.0);
            AnchorPane.setBottomAnchor(node, 0.0);
            AnchorPane.setLeftAnchor(node, 0.0);

            controller.loadRoleDashboard();

        } catch (IOException e) {
            e.printStackTrace();
            AlertUtils.showAlert("Load Error", "Could not load: " + fxmlPath);
        }
    }

    public static void setContent(AnchorPane holderPane, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneUtils.class.getResource(fxmlPath));
            Node node = loader.load();

            // Clear old content
            holderPane.getChildren().clear();

            // Add new content
            holderPane.getChildren().add(node);

            // Anchor new content to all edges
            AnchorPane.setTopAnchor(node, 0.0);
            AnchorPane.setRightAnchor(node, 0.0);
            AnchorPane.setBottomAnchor(node, 0.0);
            AnchorPane.setLeftAnchor(node, 0.0);

        } catch (IOException e) {
            e.printStackTrace();
            AlertUtils.showAlert("Load Error", "Could not load: " + fxmlPath);
        }
    }

    public static void clearScreenColor(AnchorPane pane) {
        pane.setBackground(null);
    }
}
