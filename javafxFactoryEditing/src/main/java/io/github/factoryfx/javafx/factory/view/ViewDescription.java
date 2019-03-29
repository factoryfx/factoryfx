package io.github.factoryfx.javafx.factory.view;

import io.github.factoryfx.factory.util.LanguageText;
import io.github.factoryfx.javafx.factory.util.UniformDesign;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.stage.Stage;
import org.controlsfx.glyphfont.FontAwesome;

public class ViewDescription {
    private final LanguageText text;
    private final FontAwesome.Glyph icon;
    private final UniformDesign uniformDesign;

    public ViewDescription(LanguageText text, FontAwesome.Glyph icon, UniformDesign uniformDesign) {
        this.text = text;
        this.icon = icon;
        this.uniformDesign = uniformDesign;
    }

    public void describeMenuItem(MenuItem menuItem){
        menuItem.setText(uniformDesign.getText(text));
        uniformDesign.addIcon(menuItem,icon);
    }

    public void describeTabView(Tab tab) {
        tab.setText(uniformDesign.getText(text));
        uniformDesign.addIcon(tab,icon);
    }

    public void describeStageView(Stage stage) {
        stage.setTitle(uniformDesign.getText(text));
    }
}
