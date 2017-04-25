package de.factoryfx.javafx.view;

import org.controlsfx.glyphfont.FontAwesome;

public class ViewDescription {
    public final String text;
    public final FontAwesome.Glyph icon;

    public ViewDescription(String text, FontAwesome.Glyph icon) {
        this.text = text;
        this.icon = icon;
    }
}
