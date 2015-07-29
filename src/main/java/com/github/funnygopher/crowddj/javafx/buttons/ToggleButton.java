package com.github.funnygopher.crowddj.javafx.buttons;

import javafx.event.EventHandler;

public class ToggleButton extends Button {

    private String toggleOnCss, toggleOffCss;

    public ToggleButton(javafx.scene.control.Button button, String graphicURL, EventHandler eventHandler, String toggleOnCss, String toggleOffCss) {
        super(button, graphicURL, eventHandler, toggleOffCss);
        this.toggleOnCss = this.getClass().getResource(toggleOnCss).toExternalForm();
        this.toggleOffCss = this.getClass().getResource(toggleOffCss).toExternalForm();
    }

    public void on() {
        button.getStylesheets().clear();
        button.getStylesheets().add(toggleOnCss);
    }

    public void off() {
        button.getStylesheets().clear();
        button.getStylesheets().add(toggleOffCss);
    }

    public void set(boolean value) {
        button.getStylesheets().clear();
        String style = value ? toggleOnCss : toggleOffCss;
        button.getStylesheets().add(style);
    }
}
