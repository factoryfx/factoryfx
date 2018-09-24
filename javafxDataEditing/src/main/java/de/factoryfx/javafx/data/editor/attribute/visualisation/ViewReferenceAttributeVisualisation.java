package de.factoryfx.javafx.data.editor.attribute.visualisation;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.ViewReferenceAttribute;
import de.factoryfx.javafx.data.editor.attribute.ValidationDecoration;
import de.factoryfx.javafx.data.editor.attribute.ValueAttributeVisualisation;
import de.factoryfx.javafx.data.util.UniformDesign;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.controlsfx.glyphfont.FontAwesome;

import java.util.function.Consumer;

public class ViewReferenceAttributeVisualisation<R extends Data, T extends Data, A extends ViewReferenceAttribute<R,T,A>> extends ValueAttributeVisualisation<T, A> {

    private final Consumer<Data> navigateToData;
    private final UniformDesign uniformDesign;


    private StringBinding stringBinding;

    public ViewReferenceAttributeVisualisation(A attribute, ValidationDecoration validationDecoration, Consumer<Data> navigateToData, UniformDesign uniformDesign) {
        super(attribute,validationDecoration);
        this.navigateToData = navigateToData;
        this.uniformDesign = uniformDesign;
        attribute.setRunlaterExecutor(Platform::runLater);
    }

    @Override
    public Node createValueVisualisation() {
        stringBinding = Bindings.createStringBinding(() -> {
            if (observableAttributeValue.get()!=null){
                return observableAttributeValue.get().internal().getDisplayText();
            }
            return "<empty>";
        }, observableAttributeValue);

        TextField textField = new TextField();
        textField.setEditable(false);
        textField.textProperty().bind(stringBinding);
        textField.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                if (mouseEvent.getClickCount() == 2 && observableAttributeValue.get()!=null) {
                    navigateToData.accept(observableAttributeValue.get());
                }
            }
        });

        Button editButton = new Button();
        uniformDesign.addIcon(editButton, FontAwesome.Glyph.EDIT);
        editButton.setOnAction(event -> {
            navigateToData.accept(observableAttributeValue.get());
        });
        editButton.disableProperty().bind(observableAttributeValue.isNull());

        HBox hBox =new HBox(3);
        hBox.setAlignment(Pos.TOP_LEFT);
        HBox.setHgrow(textField, Priority.ALWAYS);
        hBox.getChildren().addAll(textField, editButton);
        return hBox;
    }
}
