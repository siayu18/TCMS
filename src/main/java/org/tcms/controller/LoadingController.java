package org.tcms.controller;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import org.tcms.navigation.View;
import org.tcms.utils.SceneUtils;

public class LoadingController {

    @FXML private ProgressBar progressBar;
    @FXML private ProgressIndicator progressIndicator;
    @FXML private AnchorPane holderPane;

    @FXML
    public void initialize() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(progressBar.progressProperty(), 0),
                        new KeyValue(progressIndicator.progressProperty(), 0)
                ),
                new KeyFrame(Duration.seconds(3),
                        new KeyValue(progressBar.progressProperty(), 1),
                        new KeyValue(progressIndicator.progressProperty(), 1)
                )
        );

        timeline.setOnFinished(event -> {
            // After animation completes, go to login page
            SceneUtils.setContent(holderPane, View.LOGIN);
        });

        timeline.play();
    }
}
