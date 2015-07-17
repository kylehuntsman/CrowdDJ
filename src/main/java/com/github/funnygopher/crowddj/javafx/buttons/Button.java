package com.github.funnygopher.crowddj.javafx.buttons;

import com.github.funnygopher.crowddj.vlc.VLCStatus;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Button {
    protected javafx.scene.control.Button button;

    public Button(javafx.scene.control.Button button, String graphicURL, EventHandler eventHandler, String cssURL) {
        this.button = button;

        // Sets the buttons image
        Image image = new Image(getClass().getResourceAsStream(graphicURL));
        ImageView imageView = new ImageView(image);
        button.setGraphic(imageView);

        // What happens when the button is pressed
        button.setOnAction(eventHandler);

        button.getStylesheets().add(this.getClass().getResource(cssURL).toExternalForm());
    }

    public void update(VLCStatus status) {

    }

    public javafx.scene.control.Button getButton() {
        return button;
    }

    public void setDisable(boolean disable) {
        button.setDisable(disable);
    }

    public void setVisible(boolean visible) {
        button.setVisible(visible);
    }
}
