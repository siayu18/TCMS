package org.tcms.controller.SystemController;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public abstract class BaseDashboardController {

    @FXML
    protected Label greetingLabel;

    @FXML
    protected Label timeLabel;

    @FXML
    protected Label dateLabel;

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public void initialize() {
        String fontPath = getClass().getResource("/org/tcms/font/digital-7.ttf").toExternalForm();
        Font digitalFont = Font.loadFont(fontPath, 80);

        greetingLabel.setText(formatGreeting());
        updateDateTime();

        // Update time every minute
        Timeline clock = new Timeline(
                new KeyFrame(Duration.ZERO, e -> updateDateTime()),
                new KeyFrame(Duration.minutes(1))
        );
        clock.setCycleCount(Timeline.INDEFINITE);
        clock.play();
    }

    private void updateDateTime() {
        LocalTime now = LocalTime.now();
        LocalDate today = LocalDate.now();
        timeLabel.setText(now.format(TIME_FORMAT));
        dateLabel.setText("Date: " + today.format(DATE_FORMAT));
    }

    protected abstract String formatGreeting();
}
