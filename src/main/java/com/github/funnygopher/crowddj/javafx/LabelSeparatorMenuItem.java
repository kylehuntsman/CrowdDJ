package com.github.funnygopher.crowddj.javafx;

// Original code from tiwulfx.panemu.com

import javafx.scene.control.SeparatorMenuItem;

public class LabelSeparatorMenuItem extends SeparatorMenuItem {

    public LabelSeparatorMenuItem(String label) {
        this(label, true);
    }

    public LabelSeparatorMenuItem(String label, boolean topPadding) {
        super();
        LabelSeparator content = new LabelSeparator(label, topPadding);
        content.setPrefHeight(LabelSeparator.USE_COMPUTED_SIZE);
        content.setMinHeight(LabelSeparator.USE_PREF_SIZE);
        setContent(content);
    }
}
