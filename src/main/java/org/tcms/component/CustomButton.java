package org.tcms.component;

import javafx.scene.control.Button;

public class CustomButton extends Button {

    public static final String buttonStyle  = "-fx-background-color: #007bff; -fx-text-fill: white; -fx-font-size: 14;";

    public CustomButton(String text) {
        super(text);
        this.setStyle(buttonStyle);
        this.setPrefWidth(150);
        this.setPrefHeight(40);
    }
}

//hi