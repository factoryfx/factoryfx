package de.factoryfx.javafx.editor.attribute.visualisation;

import de.factoryfx.data.Data;
import de.factoryfx.javafx.editor.attribute.ImmutableAttributeEditorVisualisation;
import de.factoryfx.javafx.editor.data.DataEditor;
import de.factoryfx.javafx.util.UniformDesign;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.controlsfx.glyphfont.FontAwesome;

public class ViewReferenceAttributeVisualisation extends ImmutableAttributeEditorVisualisation<Data> {

    private final DataEditor dataEditor;
    private final UniformDesign uniformDesign;


    private StringBinding stringBinding;

    public ViewReferenceAttributeVisualisation(DataEditor dataEditor, UniformDesign uniformDesign) {
        this.dataEditor = dataEditor;
        this.uniformDesign = uniformDesign;
    }

    @Override
    public Node createContent(SimpleObjectProperty<Data> boundTo) {
        stringBinding = Bindings.createStringBinding(() -> {
            if (boundTo.get()!=null){
                return boundTo.get().internal().getDisplayText();
            }
            return "<empty>";
        },boundTo);

        TextField textField = new TextField();
        textField.setEditable(false);
        textField.textProperty().bind(stringBinding);
        textField.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                if (mouseEvent.getClickCount() == 2 && boundTo.get()!=null) {
                    dataEditor.edit(boundTo.get());
                }
            }
        });

        Button editButton = new Button();
        uniformDesign.addIcon(editButton, FontAwesome.Glyph.EDIT);
        editButton.setOnAction(event -> {
            dataEditor.edit(boundTo.get());
        });
        editButton.disableProperty().bind(boundTo.isNull());

        HBox hBox =new HBox(3);
        hBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(textField, Priority.ALWAYS);
        hBox.getChildren().addAll(textField, editButton);
        return hBox;
    }
}
