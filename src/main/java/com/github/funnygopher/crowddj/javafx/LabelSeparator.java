package com.github.funnygopher.crowddj.javafx;

// Original code from tiwulfx.panemu.com, modified to current JavaFX version

import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

public class LabelSeparator extends StackPane {

    private Label lblText;

    public LabelSeparator(String label) {
        this(label, true);
    }

    public LabelSeparator(String label, boolean topPadding) {
        HBox newLine = new HBox();
        newLine.getStyleClass().add("line");
        newLine.setMinHeight(2);
        newLine.setPrefHeight(2);
        newLine.setMaxHeight(USE_PREF_SIZE);
        newLine.setPrefWidth(USE_PREF_SIZE);

        if (topPadding) {
            setPadding(new Insets(10, 0, 0, 0));
        }

        lblText = new Label(label);
        this.getChildren().addAll(newLine, lblText);
        this.getStyleClass().add("label-separator");
    }

    public void setText(String label) {
        lblText.setText(label);
    }

    public String getText() {
        return lblText.getText();
    }

    public StringProperty textProperty() {
        return lblText.textProperty();
    }
}
