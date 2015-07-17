package com.github.funnygopher.crowddj.javafx.buttons;

import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class PassiveButton {

    private final String BASE_STYLE = "-fx-background-color: rgba(0,0,0,0);";
    private final String PASSIVE_STYLE = BASE_STYLE + "-fx-opacity: .5;";
    private final String HOVER_STYLE = BASE_STYLE + "-fx-opacity: 1;";

    private Button button;

    public PassiveButton(Button button, String graphicURL, EventHandler eventHandler) {
        this.button = button;

        // Sets the buttons image
        Image image = new Image(getClass().getResourceAsStream(graphicURL));
        ImageView imageView = new ImageView(image);
        button.setGraphic(imageView);

        // What happens when the button is pressed
        button.setOnAction(eventHandler);

        // The initial button style
        button.setStyle(PASSIVE_STYLE);

        // Sets the style when the mouse is hovered over the button
        button.setOnMouseEntered(handler -> button.setStyle(HOVER_STYLE));
        button.setOnMouseExited(handler -> button.setStyle(PASSIVE_STYLE));
    }
}
