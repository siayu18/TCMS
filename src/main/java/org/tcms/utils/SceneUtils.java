package org.tcms.utils;

import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import org.tcms.utils.AlertUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneUtils {
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
