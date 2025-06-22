package org.tcms.utils;

import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.fxml.FXMLLoader;
import org.tcms.navigation.View;

import java.io.IOException;

public class SceneUtils {

    // clear the screen and add the content for the desired anchorpane
    public static <T> T setContent(AnchorPane container, View view) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneUtils.class.getResource(view.getPath()));
            Node node = loader.load();
            container.getChildren().setAll(node);
            AnchorPane.setTopAnchor(node, 0.);
            AnchorPane.setBottomAnchor(node, 0.);
            AnchorPane.setLeftAnchor(node, 0.);
            AnchorPane.setRightAnchor(node, 0.);
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
