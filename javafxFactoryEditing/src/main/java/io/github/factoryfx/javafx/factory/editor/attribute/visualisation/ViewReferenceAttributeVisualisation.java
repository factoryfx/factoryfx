package io.github.factoryfx.javafx.factory.editor.attribute.visualisation;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryViewReferenceAttribute;
import io.github.factoryfx.javafx.factory.editor.attribute.ValidationDecoration;
import io.github.factoryfx.javafx.factory.editor.attribute.ValueAttributeVisualisation;
import io.github.factoryfx.javafx.factory.util.UniformDesign;
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

public class ViewReferenceAttributeVisualisation<R extends FactoryBase<?,R>,L, T extends FactoryBase<L,R>> extends ValueAttributeVisualisation<T, FactoryViewReferenceAttribute<R,L,T>> {

    private final Consumer<FactoryBase<?,?>> navigateToData;
    private final UniformDesign uniformDesign;


    private StringBinding stringBinding;

    public ViewReferenceAttributeVisualisation(FactoryViewReferenceAttribute<R,L,T> attribute, ValidationDecoration validationDecoration, Consumer<FactoryBase<?,?>> navigateToData, UniformDesign uniformDesign) {
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
