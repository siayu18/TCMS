package org.tcms;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/org/tcms/view/SystemScenes/LoginView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), stage.getHeight(), stage.getWidth());
        stage.setTitle("Tuition Centre Management System");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
