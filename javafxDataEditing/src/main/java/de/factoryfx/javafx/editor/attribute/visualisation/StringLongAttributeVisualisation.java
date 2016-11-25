package de.factoryfx.javafx.editor.attribute.visualisation;

import com.google.common.base.Ascii;
import de.factoryfx.javafx.util.UniformDesign;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.controlsfx.glyphfont.FontAwesome;

public class StringLongAttributeVisualisation extends  ExpandableAttributeVisualisation<String> {

    private final UniformDesign uniformDesign;

    public StringLongAttributeVisualisation(UniformDesign uniformDesign) {
        super(uniformDesign);
        this.uniformDesign = uniformDesign;
    }

    @Override
    protected FontAwesome.Glyph getSummaryIcon() {
        return FontAwesome.Glyph.FONT;
    }

    @Override
    protected String getSummaryText(SimpleObjectProperty<String> boundTo) {
        return Ascii.truncate(boundTo.get(),20,"...");
    }

    @Override
    protected VBox createDetailView(SimpleObjectProperty<String> boundTo) {
        TextArea textField = new TextArea();
        textField.textProperty().bindBidirectional(boundTo);
        VBox vBox = new VBox();
        VBox.setVgrow(textField, Priority.ALWAYS);
        vBox.getChildren().add(textField);
        return vBox;
    }

}
