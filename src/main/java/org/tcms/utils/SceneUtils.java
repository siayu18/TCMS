package org.tcms.utils;

import org.tcms.utils.AlertUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class SceneUtils {
    public static void switchScene(Stage stage, String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneUtils.class.getResource(fxmlPath));
            Parent root = loader.load();

            double x = stage.getX();
            double y = stage.getY();

            Scene scene = new Scene(root, 700, 500);
            stage.setScene(scene);
            stage.setTitle(title);
            stage.setX(x);
            stage.setY(y);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtils.showAlert("Scene Load Error", "Could not load: " + fxmlPath);
        }
    }
}
