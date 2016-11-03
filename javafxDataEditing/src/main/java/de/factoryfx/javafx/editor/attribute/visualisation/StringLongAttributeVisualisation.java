package de.factoryfx.javafx.editor.attribute.visualisation;

import com.google.common.base.Ascii;
import de.factoryfx.javafx.editor.attribute.AttributeEditorVisualisation;
import de.factoryfx.javafx.util.UniformDesign;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.controlsfx.glyphfont.FontAwesome;

public class StringLongAttributeVisualisation implements AttributeEditorVisualisation<String> {

    private final UniformDesign uniformDesign;

    public StringLongAttributeVisualisation(UniformDesign uniformDesign) {
        this.uniformDesign = uniformDesign;
    }

    @Override
    public Node createContent(SimpleObjectProperty<String> boundTo) {
        VBox detailView = createDetailView(boundTo);
        return uniformDesign.createExpandableEditorWrapper(boundTo,detailView, FontAwesome.Glyph.FONT, t -> Ascii.truncate(boundTo.get(),20,"..."));
    }


    private VBox createDetailView(SimpleObjectProperty<String> boundTo) {
        TextArea textField = new TextArea();
        textField.textProperty().bindBidirectional(boundTo);
        VBox vBox = new VBox();
        VBox.setVgrow(textField, Priority.ALWAYS);
        vBox.getChildren().add(textField);
        return vBox;
    }

}
