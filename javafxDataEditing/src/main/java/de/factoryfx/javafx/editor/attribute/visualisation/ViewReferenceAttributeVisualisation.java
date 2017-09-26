package de.factoryfx.javafx.editor.attribute.visualisation;

import de.factoryfx.data.Data;
import de.factoryfx.javafx.editor.attribute.ValueAttributeEditorVisualisation;
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

import java.util.function.Consumer;

public class ViewReferenceAttributeVisualisation extends ValueAttributeEditorVisualisation<Data> {

    private final Consumer<Data> navigateToData;
    private final UniformDesign uniformDesign;


    private StringBinding stringBinding;

    public ViewReferenceAttributeVisualisation(Consumer<Data> navigateToData, UniformDesign uniformDesign) {
        this.navigateToData = navigateToData;
        this.uniformDesign = uniformDesign;
    }

    @Override
    public Node createVisualisation(SimpleObjectProperty<Data> boundTo, boolean readonly) {
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
                    navigateToData.accept(boundTo.get());
                }
            }
        });

        Button editButton = new Button();
        uniformDesign.addIcon(editButton, FontAwesome.Glyph.EDIT);
        editButton.setOnAction(event -> {
            navigateToData.accept(boundTo.get());
        });
        editButton.disableProperty().bind(boundTo.isNull());

        HBox hBox =new HBox(3);
        hBox.setAlignment(Pos.TOP_LEFT);
        HBox.setHgrow(textField, Priority.ALWAYS);
        hBox.getChildren().addAll(textField, editButton);
        return hBox;
    }
}
