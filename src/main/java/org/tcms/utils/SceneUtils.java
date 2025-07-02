package org.tcms.utils;

import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.fxml.FXMLLoader;
import org.tcms.navigation.View;

import java.io.IOException;

public class SceneUtils {

    // clear the screen and add the content for the desired AnchorPane
    // type <T> means I can choose whether to return or not returning value (return if needed)
    public static <T> T setContent(AnchorPane container, View view) {
        try {
            // loads the FXML file
            FXMLLoader loader = new FXMLLoader(SceneUtils.class.getResource(view.getPath()));
            Node node = loader.load();

            // clears all previous nodes and set the loaded fxml file's nodes into the scene
            container.getChildren().setAll(node);

            // Anchors the node to all four sides (top, bottom, left, right) to make it fill the entire AnchorPane
            AnchorPane.setTopAnchor(node, 0.0);
            AnchorPane.setBottomAnchor(node, 0.0);
            AnchorPane.setLeftAnchor(node, 0.0);
            AnchorPane.setRightAnchor(node, 0.0);

            // return controller if needed
            return loader.getController();
        } catch (IOException e) {
            throw new RuntimeException("Could not load " + view, e);
        }
    }

    // remove screen background colour
    public static void clearScreenColor(AnchorPane pane) {
        pane.setBackground(null);
    }
}