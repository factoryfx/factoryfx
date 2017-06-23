package de.factoryfx.javafx.editor.attribute;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.attribute.AttributeChangeListener;
import de.factoryfx.data.validation.ValidationError;
import de.factoryfx.javafx.util.UniformDesign;
import de.factoryfx.javafx.widget.Widget;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;

public class AttributeEditor<T,A extends Attribute<T,A>> implements Widget {

    private final AttributeEditorVisualisation<T> attributeEditorVisualisation;
    private final A boundAttribute;
    private final UniformDesign uniformDesign;
    private Consumer<List<ValidationError>> validationUpdater;
    private List<ValidationError> validationErrors=new ArrayList<>();
    private boolean readonly=false;

    public AttributeEditor(A boundAttribute, AttributeEditorVisualisation<T> attributeEditorVisualisation, UniformDesign uniformDesign) {
        this.boundAttribute=boundAttribute;
        this.attributeEditorVisualisation=attributeEditorVisualisation;
        attributeEditorVisualisation.init(boundAttribute);
        this.uniformDesign = uniformDesign;
    }

    private final AttributeChangeListener<T,A> attributeChangeListener = (attribute, value) -> {
//        Platform.runLater(()-> {
            AttributeEditor.this.attributeEditorVisualisation.attributeValueChanged(value);
//        });
    };

    public void expand() {
        attributeEditorVisualisation.expand();
    }

    public void reportValidation(List<ValidationError> attributeValidationErrors){
        validationErrors=attributeValidationErrors;
        if (validationUpdater!=null){
            validationUpdater.accept(attributeValidationErrors);
        }
    }

    Node content;
    @Override
    public Node createContent() {
        boundAttribute.internal_addListener(attributeChangeListener);
        attributeChangeListener.changed(boundAttribute,boundAttribute.get());
        if (content==null){
            Node visualisation;
            if (readonly){
                visualisation = attributeEditorVisualisation.createReadOnlyVisualisation();
            } else {
                visualisation = attributeEditorVisualisation.createVisualisation();
            }

            content = addValidationDecoration(visualisation);
            attributeEditorVisualisation.attributeValueChanged(boundAttribute.get());
        }
        return content;
    }

    public void unbind() {
        content=null;
        boundAttribute.internal_removeListener(attributeChangeListener);
    }

    public Node addValidationDecoration(Node node) {

        if (node == null)
            return null;

        ContextMenu validationPopupWorkaround = new ContextMenu();//workaround for missing javax feature https://bugs.openjdk.java.net/browse/JDK-8090477
        validationPopupWorkaround.getStyleClass().add("errorContextMenu");
        MenuItem menuItem = new MenuItem();
        Label validationTest = new Label();
        menuItem.setGraphic(validationTest);
        validationPopupWorkaround.getItems().add(menuItem);

        validationUpdater = (validationErrors) -> {
            boolean isValid = validationErrors.isEmpty();
            StringBuilder validationErrorText = new StringBuilder();
            int counter = 1;
            for (ValidationError validationError : validationErrors) {
                if (validationError.isErrorFor(boundAttribute)) {
                    validationErrorText.append(counter);
                    validationErrorText.append(": ");
                    validationErrorText.append(validationError.validationDescription(uniformDesign::getText));
                    validationErrorText.append("\n");
                    counter++;
                }
            }


            final List<ValidationError> childErrors = validationErrors.stream().filter(e -> !e.isErrorFor(boundAttribute)).collect(Collectors.toList());
            if (!childErrors.isEmpty()){
                counter = 1;
                validationErrorText.append("Error in Reference:\n");
                for (ValidationError validationError : childErrors) {
                    validationErrorText.append(counter);
                    validationErrorText.append(": ");
                    validationErrorText.append(validationError.validationDescriptionForChild(uniformDesign.getLocale()));
                    validationErrorText.append("\n");
                    counter++;
                }
            }


            if (!isValid && !node.isDisabled()) {
                validationTest.setText(validationErrorText.toString());
                menuItem.setGraphic(validationTest);
                if (node.getStyleClass().stream().noneMatch(c -> c.equals("error"))) node.getStyleClass().add("error");
                node.setOnMouseEntered(event -> {
                    if (!validationPopupWorkaround.isShowing()) {
                        validationPopupWorkaround.show(node, Side.BOTTOM, 0, 0);
                    }
                });
                node.setOnMouseExited(event -> validationPopupWorkaround.hide());
                if (node.isFocused() && !validationPopupWorkaround.isShowing()) {
                    validationPopupWorkaround.show(node, Side.BOTTOM, 0, 0);
                }
            } else {
                node.getStyleClass().removeIf(c -> c.equals("error"));
                node.setOnMouseEntered(null);
                node.setOnMouseExited(null);
                validationPopupWorkaround.hide();
            }
        };
        validationUpdater.accept(validationErrors);

        return node;
    }

    public void setReadOnly() {
        this.readonly=true;
    }
}
