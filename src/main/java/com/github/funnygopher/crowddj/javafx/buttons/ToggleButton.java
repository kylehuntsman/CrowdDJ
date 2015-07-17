package com.github.funnygopher.crowddj.javafx.buttons;

import com.github.funnygopher.crowddj.vlc.VLCStatus;
import javafx.event.EventHandler;

public class ToggleButton extends Button {

    private String toggleOnCss, toggleOffCss;

    public ToggleButton(javafx.scene.control.Button button, String graphicURL, EventHandler eventHandler, String toggleOnCss, String toggleOffCss) {
        super(button, graphicURL, eventHandler, toggleOffCss);
        this.toggleOnCss = toggleOnCss;
        this.toggleOffCss = toggleOffCss;
    }

    @Override
    public void update(VLCStatus status) {
        super.update(status);
        String css;
        if(status.isRandom())
            css = this.getClass().getResource(toggleOnCss).toExternalForm();
        else
            css = this.getClass().getResource(toggleOffCss).toExternalForm();

        if(!button.getStylesheets().contains(css)) {
            button.getStylesheets().clear();
            button.getStylesheets().add(css);
        }
    }
}
