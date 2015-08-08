package com.github.funnygopher.crowddj.javafx.buttons;

import javafx.beans.value.ObservableBooleanValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ButtonUtil {

    public static void SetupButton(Button button, EventHandler<ActionEvent> event, Image graphic, String css) {
        ImageView imageView = new ImageView(graphic);
        button.setGraphic(imageView);
        button.setOnAction(event);
        button.getStylesheets().add(css);
    }

    public static void SetupToggleButton(Button button, EventHandler<ActionEvent> event, ObservableBooleanValue property, Image graphic, String onCss, String offCss) {
        SetupButton(button, event, graphic, offCss);
        property.addListener((observable, oldValue, newValue) -> {
            button.getStylesheets().clear();
            String css = newValue ? onCss : offCss;
            button.getStylesheets().add(css);
        });
    }
}
